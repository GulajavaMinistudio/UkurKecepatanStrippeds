package gulajava.speedcepat.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter

/**
 * Created by Gulajava Ministudio on 6/24/18.
 */
class ViewPagerAdapters(fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {

    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitles = ArrayList<String>()

    fun addFragments(fragment: Fragment, titles: String) {

        mFragmentList.add(fragment)
        mFragmentTitles.add(titles)
    }

    override fun getItem(position: Int): Fragment {

        return mFragmentList[position]
    }

    override fun getCount(): Int {

        return mFragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        super.getPageTitle(position)

        return mFragmentTitles[position]
    }

    override fun getItemPosition(objek: Any): Int {
        super.getItemPosition(objek)
        return PagerAdapter.POSITION_NONE
    }
}