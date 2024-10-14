package com.dragonguard.android.ui.login

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dragonguard.android.R
import com.dragonguard.android.data.model.klip.Bapp
import com.dragonguard.android.data.model.klip.CallBack
import com.dragonguard.android.data.model.klip.WalletAuthRequestModel
import com.dragonguard.android.databinding.ActivityLoginBinding
import com.dragonguard.android.ui.main.MainActivity
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {

    private var backPressed: Long = 0
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private val body = WalletAuthRequestModel(
        Bapp(
            "GitRank",
            CallBack()
        ), "auth"
    )

    //private var walletAddress = ""
    private lateinit var mClient: CustomTabsClient

    override fun onCreate(savedInstanceState: Bundle?) {
        CookieManager.getInstance().setAcceptCookie(true)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        //Log.d("시작", "loginactivity1")
        viewModel = LoginViewModel()
        initObserver()
        //쿠키 확인 코드
        this.onBackPressedDispatcher.addCallback(this, callback)
        val defaultBrowser = getDefaultBrowserPackageName(this@LoginActivity)
        if (defaultBrowser != null) {
            CustomTabsClient.bindCustomTabsService(this@LoginActivity, defaultBrowser, connection)
        } else {
            CustomTabsClient.bindCustomTabsService(
                this@LoginActivity,
                "com.android.chrome",
                connection
            )
        }

        Log.d("시작 token", viewModel.currentState.token.token)

        if (viewModel.currentState.token.token.isNotBlank() && viewModel.currentState.refreshToken.refreshToken.isNotBlank()) {
            val intentF = Intent(applicationContext, MainActivity::class.java)
            startActivity(intentF)
            finish()
        }
        val logout = intent.getBooleanExtra("logout", false)
        if (viewModel.currentState.token.token.isNotBlank() && viewModel.currentState.refreshToken.refreshToken.isNotBlank()) {
            //Toast.makeText(applicationContext, "jwt token : $token", Toast.LENGTH_SHORT).show()
            val intentF = Intent(applicationContext, MainActivity::class.java)
            startActivity(intentF)
            finish()
        } else {
            binding.githubAuth.isEnabled = true
//            binding.walletFinish.isEnabled = false
        }
        if (logout) {
            viewModel.setJwtToken("", "")
            binding.oauthWebView.apply {
                clearHistory()
                clearCache(true)
            }
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeSessionCookies { aBoolean ->
            }
            cookieManager.removeAllCookies(ValueCallback<Boolean?> { value ->
            })
            cookieManager.flush()
        }

        /*val address = intent.getStringExtra("wallet_address")
        address?.let {
            walletAddress = address
            if (walletAddress.isNotBlank() && !token.isNullOrEmpty()) {
                Log.d("wallet", "지갑주소 이미 있음 $walletAddress")
                Toast.makeText(applicationContext, "wallet : $walletAddress", Toast.LENGTH_SHORT).show()
                val intentW = Intent(applicationContext, MainActivity::class.java)
                setResult(1, intentW)
                finish()
            }
        }*/


        /*binding.oauthWebView.apply {
            settings.javaScriptEnabled = true // 자바스크립트 허용
            settings.javaScriptCanOpenWindowsAutomatically = false
            // 팝업창을 띄울 경우가 있는데, 해당 속성을 추가해야 window.open() 이 제대로 작동 , 자바스크립트 새창도 띄우기 허용여부
            settings.setSupportMultipleWindows(false) // 새창 띄우기 허용 여부 (멀티뷰)
            settings.loadsImagesAutomatically = true // 웹뷰가 앱에 등록되어 있는 이미지 리소스를 자동으로 로드하도록 설정하는 속성
            settings.useWideViewPort = true // 화면 사이즈 맞추기 허용 여부
            settings.loadWithOverviewMode = true // 메타태그 허용 여부
            settings.setSupportZoom(true) // 화면 줌 허용여부
            settings.builtInZoomControls = false // 화면 확대 축소 허용여부
            settings.displayZoomControls = false // 줌 컨트롤 없애기.
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            settings.domStorageEnabled =
                true // 로컬 스토리지 사용 여부를 설정하는 속성으로 팝업창등을 '하루동안 보지 않기' 기능 사용에 필요
            settings.allowContentAccess // 웹뷰 내에서 파일 액세스 활성화 여부
            settings.userAgentString = "app" // 웹에서 해당 속성을 통해 앱에서 띄운 웹뷰로 인지 할 수 있도록 합니다.
            settings.defaultTextEncodingName = "UTF-8" // 인코딩 설정
            settings.databaseEnabled = true //Database Storage API 사용 여부 설정
        }
        binding.oauthWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                if (request.url.toString().startsWith(appUrl)) {
                    val cookies = CookieManager.getInstance().getCookie(request.url.toString())
                    if (cookies != null) {
                        Log.d("cookie", "shouldoverrideurlloading : $cookies")
                    }
                    return false
                }
                Log.d("cookie", "url: ${request.url}")
                binding.oauthWebView.loadUrl(request.url.toString())
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                try {
                    if (!this@LoginActivity.isFinishing) {
                        val cookies = CookieManager.getInstance()
                            .getCookie(getString(R.string.login_url))
                        val cookie = CookieManager.getInstance()
                            .getCookie(appUrl)
                        Log.d("cookie", "$oauthUrl: $cookies  $appUrl: $cookie")
                        Log.d("cookie", "onPageFinished original url: $url")
                        Log.d("cookie", "onPageFinished original: $cookies")
                        if (cookies.contains("Access") && url!!.startsWith(appUrl)) {
                            val splits = cookies.split("; ")
                            val access = splits[1].split("=")[1]
                            val refresh = splits[2].split("=")[1]
                            Log.d("tokens", "access:$access, refresh:$refresh")
                            viewModel.setJwtToken(access, refresh)
                            *//*if (walletAddress.isNotBlank()) {
                                val intentH = Intent(this@LoginActivity, MainActivity::class.java)
                                intentH.putExtra("access", access)
                                intentH.putExtra("refresh", refresh)
                                startActivity(intentH)
                            } else {
                                binding.loginGithub.visibility = View.GONE
                                binding.loginMain.visibility = View.VISIBLE
                                binding.githubAuth.isEnabled = false
                                checkState(prefs.getJwtToken(""), prefs.getRefreshToken(""))
                            }*//*
                            val intentH = Intent(this@LoginActivity, MainActivity::class.java)
                            intentH.putExtra("access", access)
                            intentH.putExtra("refresh", refresh)
                            startActivity(intentH)
                        }
                    }
                } catch (e: Exception) {
                    Log.d("webview error", "error ${e.message}")

                }
            }
        }
        binding.oauthWebView.webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                val newWebView = WebView(view!!.context)
                val transport = resultMsg!!.obj as WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()

                newWebView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: WebResourceRequest
                    ): Boolean {
                        if (request.url.toString().startsWith(appUrl)) {
                            val cookies =
                                CookieManager.getInstance().getCookie(request.url.toString())
                            if (cookies != null) {
                                Log.d("cookie", "shouldoverrideurlloading : $cookies")
                            }
                            val intent = Intent(Intent.ACTION_VIEW, request.url)
                            startActivity(intent)
                            return false
                        }
                        binding.oauthWebView.loadUrl(request.url.toString())
                        return true
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        val cookies = CookieManager.getInstance().getCookie(url)
                        if (cookies != null) {
                            Log.d("cookie", "onPageFinished chrome: $cookies")
                        }
                    }

                }
                return true
            }
        }*/
        binding.githubAuth.setOnClickListener {
            //binding.loginMain.visibility = View.GONE
            //binding.loginGithub.visibility = View.VISIBLE
            //binding.oauthWebView.isEnabled = true
            //Log.d("이동", "웹뷰로 이동 ${BuildConfig.api}oauth2/authorize/github")
            //binding.oauthWebView.loadUrl("${BuildConfig.api}oauth2/authorize/github")

            if (::mClient.isInitialized) {
                val customTabsCallback = MyCustomTabsCallback()
                val sessions = mClient.newSession(customTabsCallback)
                val customTabsIntent = CustomTabsIntent.Builder()
                    .setSession(sessions!!)
                    .build()

                customTabsIntent.launchUrl(
                    this@LoginActivity,
                    Uri.parse(getString(R.string.login_url))
                )
            }
            //val intentG = Intent(Intent.ACTION_VIEW, Uri.parse(oauthUrl))
            //startActivity(intentG)

        }
        /*binding.walletAuth.setOnClickListener {
            if (NetworkCheck.checkNetworkState(this)) {
                walletAuthRequest()
            }
        }*/

    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.loginState.login == true) {
                        val intentF = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intentF)
                        finish()
                    }
                }
            }
        }
    }


    private val connection = object : CustomTabsServiceConnection() {
        override fun onCustomTabsServiceConnected(
            componentName: ComponentName,
            client: CustomTabsClient
        ) {
            CookieManager.getInstance().setAcceptCookie(true)
            Log.d("시작", "custom tab 시작")
            mClient = client
            mClient.warmup(0)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            // 서비스 연결 해제 시 필요한 작업을 수행합니다.
        }
    }

    private fun getDefaultBrowserPackageName(context: Context): String? {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com"))
        val resolveInfoList =
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

        for (resolveInfo in resolveInfoList) {
            // 첫 번째 매치를 사용하거나 원하는 로직에 따라 패키지를 선택할 수 있습니다.
            return resolveInfo.activityInfo.packageName
        }
        return null
    }

    inner class MyCustomTabsCallback : CustomTabsCallback() {
        override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
            /*when (navigationEvent) {
                CustomTabsCallback.NAVIGATION_STARTED -> {
                    checkCookie(navigationEvent)
                }
                CustomTabsCallback.TAB_HIDDEN -> {
                    checkCookie(navigationEvent)
                }
                CustomTabsCallback.NAVIGATION_FINISHED -> { checkCookie(navigationEvent)}
                else -> {checkCookie(navigationEvent)}
                // 기타 필요한 경우 처리
            }*/
        }

    }


    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (System.currentTimeMillis() > backPressed + 2500) {
                backPressed = System.currentTimeMillis()
                binding.loginGithub.visibility = View.GONE
                binding.loginMain.visibility = View.VISIBLE
                binding.oauthWebView.isEnabled = false
                Toast.makeText(applicationContext, "Back 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT)
                    .show()
                return
            }

            if (System.currentTimeMillis() <= backPressed + 2500) {
                finishAffinity()
            }
        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val uri = intent?.data
        Log.d("uri new", "uri: $uri")
        if (uri != null && uri.toString().startsWith("${getString(R.string.app_url)}?")) {
            val urlSplit = uri.toString().split("?")
            val queries = urlSplit[1].split("&")
            val access = queries[0].split("=")[1]
            val refresh = queries[1].split("=")[1]
            Log.d("tokens", "access:$access, refresh:$refresh")
            viewModel.setJwtToken(access, refresh)
            binding.loginMain.visibility = View.VISIBLE
            val intentF = Intent(applicationContext, MainActivity::class.java)
            startActivity(intentF)
            finish()
        } else {

        }
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.unbindService(connection)
    }
}