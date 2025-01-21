package com.ironsource.adapters.vungle.interstitial

import com.ironsource.mediationsdk.logger.IronLog
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.sdk.InterstitialSmashListener
import com.ironsource.mediationsdk.utils.ErrorBuilder
import com.ironsource.mediationsdk.utils.IronSourceConstants
import com.vungle.ads.BaseAd
import com.vungle.ads.InterstitialAdListener
import com.vungle.ads.VungleError
import java.lang.ref.WeakReference

class VungleInterstitialAdListener(
    private val mAdapter: WeakReference<VungleInterstitialAdapter>,
    private val mListener: InterstitialSmashListener,
    private val mPlacementId: String
) : InterstitialAdListener {

    /**
     * Callback used to notify that the advertisement assets have been downloaded and are ready to play.
     *
     * @param baseAd - identifier for which the advertisement assets have been downloaded.
     */
    override fun onAdLoaded(baseAd: BaseAd) {
        IronLog.ADAPTER_CALLBACK.verbose("placementId = $mPlacementId")
        mAdapter.get()?.setInterstitialAdAvailability(mPlacementId,true)
        mListener.onInterstitialAdReady()
    }

    /**
     * Callback used to notify that an error has occurred while downloading assets. This indicates an unrecoverable error within the SDK, such as lack of network or out of disk space on the device.
     *
     * @param baseAd  - identifier for which the advertisement for which the error occurred.
     * @param adError - Error message and event suggesting the cause of failure
     */
    override fun onAdFailedToLoad(baseAd: BaseAd, adError: VungleError) {
        IronLog.ADAPTER_CALLBACK.verbose("placementId = $mPlacementId, errorCode= ${adError.code} error = ${adError.errorMessage}")
        mAdapter.get()?.setInterstitialAdAvailability(mPlacementId,false)
        val adapterError = "${adError.errorMessage}( ${adError.code} )"

        val error = if (adError.code == VungleError.NO_SERVE) {
            IronSourceError(IronSourceError.ERROR_IS_LOAD_NO_FILL, adapterError)
        } else {
            ErrorBuilder.buildLoadFailedError(adapterError)
        }

        mListener.onInterstitialAdLoadFailed(error)
    }

    /**
     * Called when the Vungle SDK has successfully launched the advertisement and an advertisement will begin playing momentarily.
     *
     * @param baseAd - identifier for which the advertisement being played.
     */
    override fun onAdStart(baseAd: BaseAd) {
        IronLog.ADAPTER_CALLBACK.verbose("placementId = $mPlacementId")
        mListener.onInterstitialAdShowSucceeded()
    }

    /**
     * Called when the Vungle SDK has shown an Ad for the play/render request For Vungle adImpression should be same as adStart as we show every Ad only once, hence a unique Ad can never generate multiple impressions
     *
     * @param baseAd - identifier for which the advertisement being played.
     */
    override fun onAdImpression(baseAd: BaseAd) {
        IronLog.ADAPTER_CALLBACK.verbose("placementId = $mPlacementId")
        mListener.onInterstitialAdOpened()
    }

    /**
     * Callback used to notify that an error has occurred while playing advertisement. This indicates an unrecoverable error within the SDK, such as lack of network or out of disk space on the device.
     *
     * @param baseAd  - identifier for which the advertisement for which the error occurred.
     * @param adError - Error message and event suggesting the cause of failure
     */
    override fun onAdFailedToPlay(baseAd: BaseAd, adError: VungleError) {
        IronLog.ADAPTER_CALLBACK.verbose("placementId = $mPlacementId, errorCode = ${adError.code}, errorMessage = ${adError.message}")
        val errorMessage = " reason = " + adError.errorMessage + " errorCode = " + adError.code
        val error: IronSourceError = ErrorBuilder.buildShowFailedError(
            IronSourceConstants.INTERSTITIAL_AD_UNIT,
            errorMessage
        )
        mListener.onInterstitialAdShowFailed(error)
    }

    /**
     * Callback for an advertisement tapped. Sent when the user has tapped on an ad.
     *
     * @param baseAd - identifier for which the advertisement that was clicked.
     */
    override fun onAdClicked(baseAd: BaseAd) {
        IronLog.ADAPTER_CALLBACK.verbose("placementId = $mPlacementId")
        mListener.onInterstitialAdClicked()
    }

    /**
     * Callback when the user has left the app.
     *
     * @param baseAd - identifier for which the advertisement that user clicked resulting in leaving app
     */
    override fun onAdLeftApplication(baseAd: BaseAd) {
        IronLog.ADAPTER_CALLBACK.verbose("placementId = $mPlacementId")
    }

    /**
     * Callback for an advertisement ending. The Vungle SDK has finished playing the advertisement and the user has closed the advertisement.
     *
     * @param baseAd - identifier for which the advertisement that ended.
     */
    override fun onAdEnd(baseAd: BaseAd) {
        IronLog.ADAPTER_CALLBACK.verbose("placementId = $mPlacementId")
        mListener.onInterstitialAdClosed()
    }
}