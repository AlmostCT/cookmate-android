package almostct.top.foodhack.ui

import almostct.top.foodhack.R
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class OtherFragment() : Fragment() {
    companion object {
        operator fun invoke() = OtherFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.other_fragment, container, false)
    }
}
