package gulajava.speedcepat.splashscreen

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import gulajava.speedcepat.GlideApp
import gulajava.speedcepat.R
import gulajava.speedcepat.mainmenus.MainMenuActivity
import kotlinx.android.synthetic.main.splash_loading.*

/**
 * Created by Gulajava Ministudio on 5/12/18.
 */
class LoadingSplashActivity : AppCompatActivity(), LoadingSplashContract.View {

    private var mContext: Context? = null

    private var loadingViewModel: LoadingViewModel? = null
    private var mObserverStatusInit: Observer<Boolean>? = null

    private var mHandler: Handler? = null
    private var mRunnableJedaInit: Runnable? = null

    private var mImageViewGambarSplash: ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_loading)

        mContext = this@LoadingSplashActivity

        initViewWidget()
        initViewModel()

        initListener()

        initDataAwal()

        initTampilan()
    }

    override fun onStart() {
        super.onStart()
        initObservers()

        loadingViewModel?.initSubscriber()
        mHandler?.postDelayed(mRunnableJedaInit, 1000)
    }

    override fun onStop() {
        super.onStop()
        stopObservers()
        loadingViewModel?.stopSubscriber()
    }

    override fun initViewModel() {

        loadingViewModel =
                ViewModelProviders.of(this@LoadingSplashActivity).get(LoadingViewModel::class.java)
    }

    override fun initObservers() {

        mObserverStatusInit = Observer { isInitAwalOk: Boolean? ->
            if (isInitAwalOk == true) {
                pindahHalaman()
            } else {
                cekDatabaseAwal()
            }
        }

        mObserverStatusInit?.let {
            loadingViewModel?.getStatusInit()?.observe(this@LoadingSplashActivity, it)
        }
    }

    override fun stopObservers() {

        loadingViewModel?.getStatusInit()?.removeObservers(this@LoadingSplashActivity)
    }

    override fun initDataAwal() {

        mHandler = Handler()
    }

    override fun initListener() {

        mRunnableJedaInit = Runnable {
            cekDatabaseAwal()
        }
    }

    override fun initViewWidget() {

        mImageViewGambarSplash = gambar_splash
    }

    override fun initTampilan() {

        mImageViewGambarSplash?.let {
            GlideApp.with(this@LoadingSplashActivity).load(R.drawable.web_hi_res_512)
                .into(it)
        }
    }

    override fun cekDatabaseAwal() {

        loadingViewModel?.cekDatabaseAwal()
    }

    override fun pindahHalaman() {

        mHandler?.postDelayed(
            {
                val intent: Intent? = mContext?.let { Intent(it, MainMenuActivity::class.java) }
                intent?.let { this@LoadingSplashActivity.startActivity(it) }
                this@LoadingSplashActivity.finish()

            }, 1000
        )
    }
}