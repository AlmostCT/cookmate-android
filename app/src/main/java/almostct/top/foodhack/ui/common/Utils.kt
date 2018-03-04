package almostct.top.foodhack.ui.common

import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Observable<T>.defaultSub(handler: (T) -> Unit) = subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(handler, { Log.e("RX", "Network request failed", it) })
