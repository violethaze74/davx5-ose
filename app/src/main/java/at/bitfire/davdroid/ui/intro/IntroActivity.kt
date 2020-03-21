package at.bitfire.davdroid.ui.intro

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import at.bitfire.davdroid.R
import at.bitfire.davdroid.settings.Settings
import at.bitfire.davdroid.ui.intro.IIntroFragmentFactory.ShowMode
import com.github.paolorotolo.appintro.AppIntro2
import java.util.*

class IntroActivity: AppIntro2() {

    companion object {

        private val serviceLoader = ServiceLoader.load(IIntroFragmentFactory::class.java)!!
        private val introFragmentFactories = serviceLoader.toList()

        fun shouldShowIntroActivity(context: Context): Boolean {
            val settings = Settings.getInstance(context)
            return introFragmentFactories.any { it.shouldBeShown(context, settings) == ShowMode.SHOW }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settings = Settings.getInstance(this)

        val factoriesWithMode = introFragmentFactories.associate { Pair(it, it.shouldBeShown(this, settings)) }
        val showAll = factoriesWithMode.values.any { it == ShowMode.SHOW }
        factoriesWithMode.forEach { factory, mode ->
            if (mode == ShowMode.SHOW || (mode == ShowMode.SHOW_NOT_ALONE && showAll))
                addSlide(factory.create())
        }

        setBarColor(resources.getColor(R.color.primaryDarkColor))
        showSkipButton(false)
    }


    override fun onBackPressed() {
        if (pager.isFirstSlide(fragments.size))
            setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        setResult(Activity.RESULT_OK)
        finish()
    }

}