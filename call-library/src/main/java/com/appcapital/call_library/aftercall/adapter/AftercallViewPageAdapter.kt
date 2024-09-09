import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.appcapital.call_library.aftercall.MoreCallsControlFragment
import com.appcapital.call_library.aftercall.MoreOptionsFragment
import com.appcapital.call_library.aftercall.WeatherCardFragment


class AftercallViewPageAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        Log.d("PositionOnClick","-${position}")
        when (position) {
            0 -> return WeatherCardFragment.newInstance("","")
            1 -> return MoreCallsControlFragment.newInstance("","")
            2 -> return MoreOptionsFragment.newInstance("","")
            3 -> return MoreOptionsFragment.newInstance("","")
        }
        return MoreOptionsFragment()
    }

}