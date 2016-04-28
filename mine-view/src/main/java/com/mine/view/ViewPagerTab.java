package com.mine.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.ViewGroup;

/** 实现跟随ViewPager页面滑动的滑动条  
 *   注意：因为实现的过程要给ViewPager添加 OnchangerListener监听，使用ViewPager也会添加监听，而监听只能有一个起作用，本类中的监听是必须的。
 *   这里解决的办法是 在本类中再提供OnpageChangerLIstener 中的接口方法，可通过实现接口以实现在其它的地方也能监听的效果
 * @author Administrator
 *
 */
public class ViewPagerTab extends ViewGroup implements OnPageChangeListener{

	private ViewPager mviewPager;
	private int mWidth,mHeight;
	//当前 page 的页数 
	private int PAGECOUNT;
	private OnPageChangeListener_vp changeListener_vp;

	public ViewPagerTab(Context context,AttributeSet set) {
		super(context,set);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if(getChildCount()>0){
			getChildAt(0).layout(0, 0, mWidth/PAGECOUNT-2, mHeight);//切割导航条
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.mWidth=MeasureSpec.getSize(widthMeasureSpec);
		this.mHeight=MeasureSpec.getSize(heightMeasureSpec);
	}

	//必须
	public void setViewPager(ViewPager pager){
		this.mviewPager=pager;
		this.mviewPager.setOnPageChangeListener(this);
		this.PAGECOUNT=pager.getAdapter().getCount();
	}

	public void setOnPageChangerListener(OnPageChangeListener_vp listener_vp){
		this.changeListener_vp=listener_vp;
	}

	//页面切换

	@Override
	public void onPageScrollStateChanged(int arg0) {
		if(changeListener_vp!=null)
			changeListener_vp.onPageScrollStateChanged(arg0);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		//滑动
		scrollTo(- position * mWidth / PAGECOUNT - Math.round(positionOffset * mWidth / PAGECOUNT), 0);
		if(changeListener_vp!=null)
			changeListener_vp.onPageScrolled(position, positionOffset, positionOffsetPixels);
	}

	@Override
	public void onPageSelected(int position) {
		if(changeListener_vp!=null)
			changeListener_vp.onPageSelected(position);
	}

	//向外传递事件
	public static abstract interface OnPageChangeListener_vp{
		void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);
		void onPageScrollStateChanged(int arg0);
		void onPageSelected(int position);
	}

}
