package net.mikaelzero.mojito.impl

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.gyf.immersionbar.ImmersionBar
import net.mikaelzero.mojito.R
import net.mikaelzero.mojito.interfaces.IMojitoFragment
import net.mikaelzero.mojito.loader.FragmentCoverLoader
import net.mikaelzero.mojito.tools.Utils
import kotlin.math.roundToInt

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/18 10:59 AM
 * @Description:
 */
class DefaultTargetFragmentCover : FragmentCoverLoader {
    var view: View? = null
    private var originBottomMargin = 10
    private var currentBottomMargin = originBottomMargin

    override fun attach(iMojitoFragment: IMojitoFragment, autoLoadTarget: Boolean): View? {
        if (autoLoadTarget) {
            return null
        }
        originBottomMargin = Utils.dip2px(iMojitoFragment.providerContext()?.context, 16f) + ImmersionBar.getStatusBarHeight(iMojitoFragment.providerContext()!!)
        view = LayoutInflater.from(iMojitoFragment.providerContext()?.context).inflate(R.layout.default_target_cover_layout, null)

        val seeTargetImageTv = view?.findViewById<TextView>(R.id.seeTargetImageTv)
        val indexLp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        indexLp.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        indexLp.topMargin = originBottomMargin
        view?.layoutParams = indexLp
        seeTargetImageTv?.setOnClickListener {
            iMojitoFragment.loadTargetUrl()
        }
        view?.visibility = View.GONE
        return view
    }

    // true  has cache
    override fun imageCacheHandle(isCache: Boolean, hasTargetUrl: Boolean) {
        if (hasTargetUrl) {
            if (isCache) {
                view?.visibility = View.GONE
            } else {
                view?.visibility = View.VISIBLE
            }
        } else {
            view?.visibility = View.GONE
        }
    }

    override fun fingerRelease(isToMax: Boolean, isToMin: Boolean) {
        if (view == null || view!!.visibility == View.GONE) {
            return
        }
        var begin = 0
        var end = 0
        if (isToMax) {
            begin = currentBottomMargin
            end = originBottomMargin
        }
        if (isToMin) {
            view?.visibility = View.GONE
            return
        }
        val indexLp = view?.layoutParams as FrameLayout.LayoutParams
        val valueAnimator = ValueAnimator.ofInt(begin, end)
        valueAnimator.addUpdateListener { animation ->
            indexLp.topMargin = animation.animatedValue as Int
            view?.layoutParams = indexLp
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {}
        })
        valueAnimator.setDuration(300).start()
    }


    override fun move(moveX: Float, moveY: Float) {
        if (view == null || view!!.visibility == View.GONE) {
            return
        }
        val indexLp = view?.layoutParams as FrameLayout.LayoutParams
        currentBottomMargin = (originBottomMargin - moveY / 6f).roundToInt()
        if (currentBottomMargin > originBottomMargin) {
            currentBottomMargin = originBottomMargin
        }
        indexLp.topMargin = currentBottomMargin
        view?.layoutParams = indexLp
    }
}