package com.example.administrator.rxjavademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

/**
 * 解决rxJava在取消订阅后，上游线程发生崩溃后,下游无法捕获导致的崩溃
 */
public class RxJavaCrash extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RxJavaCrash";
    private Disposable mSubscribe;
    private boolean mCrash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_test);
        setRxJavaErrorHandler();
        findViewById(R.id.start).setOnClickListener(this);
        findViewById(R.id.crash_thread).setOnClickListener(this);
        findViewById(R.id.unsubscribe).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start: {
                Log.e(TAG, "开始订阅");
                mSubscribe = Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        int num = 0;
                        while (true) {
                            e.onNext(num++);
                            Log.e(TAG, "发射数据  " + num);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException exception) {
                                Log.e(TAG, "发射端中断异常:" + exception.toString());
                            }
                            if (mCrash) {
                                mCrash = false;
                                throw new Exception("");
                            }
                        }

                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer s) throws Exception {
                                Log.e(TAG, "接收到的数据" + s);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e(TAG, "接收到异常:" + throwable.toString());
                            }
                        });


                break;
            }
            case R.id.unsubscribe: {
                mSubscribe.dispose();
                Log.e(TAG, "取消订阅");
                break;
            }
            case R.id.crash_thread: {
                mCrash = true;
                Log.e(TAG, "发射异常");
                break;
            }
        }
    }

    /**
     * 仅仅用来捕获一些在dispose后，仍然会被发送过来的异常
     * 参考：
     * https://github.com/ReactiveX/RxJava/issues/5425
     * https://blog.csdn.net/sr_code_plus/article/details/77189478
     * https://www.aliyun.com/jiaocheng/7318.html
     */
    private void setRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                //仅仅用来捕获一些在dispose后，仍然会被发送过来的异常，此处暂时不对异常做处理
                Log.e(TAG, "接收到dispose后的异常" + (throwable == null ? "" : throwable.toString()));
            }
        });
    }
}
