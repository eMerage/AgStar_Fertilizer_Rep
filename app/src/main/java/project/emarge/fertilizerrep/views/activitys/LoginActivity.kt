package project.emarge.fertilizerrep.views.activitys

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_login.*
import project.emarge.fertilizerrep.R
import project.emarge.fertilizerrep.databinding.ActivityLoginBinding
import project.emarge.fertilizerrep.models.datamodel.Rep
import project.emarge.fertilizerrep.viewModels.login.LoginViewModel

class LoginActivity : AppCompatActivity() {

    lateinit var bindingLogin: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        bindingLogin = DataBindingUtil.setContentView<ActivityLoginBinding>(this, R.layout.activity_login)
        bindingLogin.login = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        bindingLogin.login!!.setApplicationVersion()


        bindingLogin.login!!.checkUserCredential().observe(this, Observer<Boolean> {
            it?.let { result ->
                if (result) {
                    val intent = Intent(this, VisitsActivity::class.java)
                    val bndlanimation =
                        ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out).toBundle()
                    startActivity(intent, bndlanimation)
                    this.finish()
                } else {

                    Handler().postDelayed(Runnable {
                        translateLogo()
                    }, 3000)
                }

            }
        })


        bindingLogin.login!!.loginValidationError.observe(this, Observer<String> {
            it?.let { result ->
                if (result == "ok") {
                    bindingLogin.login!!.getLoginResponsFromServer().observe(this, Observer<Rep> { loginUser ->
                        loginUser?.let { resultUser ->
                            progressBarToButton.visibility = View.GONE
                            if (resultUser.userStatus) {

                                val intent = Intent(this, VisitsActivity::class.java)
                                val bndlanimation =
                                    ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
                                        .toBundle()
                                startActivity(intent, bndlanimation)
                                super.onBackPressed()

                                Toast.makeText(this, "Welcome " + resultUser.name, Toast.LENGTH_LONG).show()
                            } else {
                                button.visibility = View.VISIBLE

                                val alertDialogBuilder = AlertDialog.Builder(this)
                                alertDialogBuilder.setTitle(resultUser.loginNetworkError.errorTitle)
                                alertDialogBuilder.setMessage(resultUser.loginNetworkError.errorMessage)
                                alertDialogBuilder.setPositiveButton(
                                    "Re-Try"
                                ) { _, _ ->
                                    return@setPositiveButton
                                }
                                alertDialogBuilder.show()
                            }
                        }
                    })
                } else {
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                }
            }
        })


    }

    private fun translateLogo() {
        val ty1 = ObjectAnimator.ofFloat(imageView_logo, View.TRANSLATION_Y, 0f, -300f)
        ty1.duration = 700
        ty1.interpolator = AccelerateInterpolator() as TimeInterpolator?
        ty1.start()
        scale()

    }


    private fun translateLogin() {
        val ty12 = ObjectAnimator.ofFloat(linearLayout_login, View.TRANSLATION_Y, 600f, 0f)
        ty12.duration = 700
        ty12.interpolator = AccelerateInterpolator()
        ty12.start()

    }


    private fun scale() {
        val anims = AnimatorSet()
        val sX = ObjectAnimator.ofFloat(imageView_logo, View.SCALE_X, 1.0f, 0.7f)
        val sY = ObjectAnimator.ofFloat(imageView_logo, View.SCALE_Y, 1.0f, 0.7f)
        anims.playTogether(sX, sY)
        anims.duration = 700
        anims.interpolator = AccelerateInterpolator()
        anims.start()
        progressBar.visibility = View.GONE
        linearLayout_login.visibility = View.VISIBLE
        translateLogin()

    }

    override fun onBackPressed() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Exit!")
        alertDialogBuilder.setMessage("Do you really want to exit ?")
        alertDialogBuilder.setPositiveButton(
            "YES"
        ) { _, _ -> super.onBackPressed() }
        alertDialogBuilder.setNegativeButton("NO", DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
        alertDialogBuilder.show()

    }

}
