package com.liuhai.expandeacherotherviewgroup

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.util.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.ScrollingView
import androidx.customview.widget.ViewDragHelper

/**
 * 作者：liuhai
 * 时间：2019/9/20:17:39
 * 邮箱：185587041@qq.com
 * 说明：
 */
class ExpendEacherOtherViewGroup : ViewGroup {

    var MinHeight =TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,100f,Resources.getSystem().displayMetrics).toInt();


    val display11: DisplayMetrics


    val scrollviews= arrayListOf<View>()
     var fristInit:Boolean=false

    init {
        display11 = DisplayMetrics();
    }
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        Log.d("重绘", "${getChildAt(0).measuredHeight}")
        display.getMetrics(display11);
        val height = MeasureSpec.makeMeasureSpec(display11.heightPixels, MeasureSpec.EXACTLY)
        val width = MeasureSpec.makeMeasureSpec(display11.widthPixels, MeasureSpec.EXACTLY);
        setMeasuredDimension(height, width)

    }




    fun addview(scrollview:View){
        scrollviews.add(scrollview)


    }


    fun remove(scrollview: View)=scrollviews.remove(scrollview)



    /**
     *  更细子view的布局
     */
    fun setLayout(view: View, height: Int) {
        val layout = view.layoutParams;
        layout.height = height
        view.layoutParams = layout

    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        val prsize = height / childCount
        var tottalHeight = 0;
        //简单布局一下
        for (i in 0 until childCount) {
            val childview = getChildAt(i)
            if (!fristInit) {
                childview.layout(0, tottalHeight, width, tottalHeight + prsize)
                setLayout(childview, prsize);
                tottalHeight += prsize;
            } else

            {
                childview.layout(0, tottalHeight, width, tottalHeight + childview.height)
             //   setLayout(childview, childview.height);
                tottalHeight += childview.height
            }
        }
        fristInit=true
    }


    lateinit var redview: ViewGroup

    lateinit var greenview: ViewGroup

    val dragHelper by lazy {
        ViewDragHelper.create(this, 1.0f,ExpendEacherOtherViewDragHelper(this));
    }

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {

    }

    constructor(context: Context, attributeSet: AttributeSet, defstyle: Int) : super(
        context,
        attributeSet,
        defstyle
    ) {
    }


    /**
     * 标准写法判断拦截
     */
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        val action = ev?.actionMasked;
        return when (action) {
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                dragHelper.cancel()
                return false
            }
            else -> {
                //如果是
                var shouldIntercept=true;
                scrollviews.forEach {
                    var rect= Rect()
                    it.getHitRect(rect)
                    //判断这个点是不在在这个VIEW之中
                   shouldIntercept= !(rect.contains(ev!!.x.toInt(),ev!!.y.toInt())&&(it is ScrollingView))

                   if( it is ScrollingView){
                       Log.d("挡墙的滚动信息","${it.computeVerticalScrollRange()}....${it.computeVerticalScrollExtent()}>>>${it.computeVerticalScrollOffset()}")
                      if(it.computeVerticalScrollRange()==(it.computeVerticalScrollExtent()+it.computeVerticalScrollOffset())){
                          shouldIntercept=true
                      }






                   }

                }
                if(shouldIntercept){
                  return  dragHelper.shouldInterceptTouchEvent(ev!!);
                }
                  return false

            }
        }
    }


    /**
     * 标准写法接管触摸事件
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        dragHelper.processTouchEvent(event!!);
        return true;
    }


    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            postInvalidate()
        }
    }


    override fun onFinishInflate() {
        super.onFinishInflate()

        redview = findViewById(R.id.top);

        greenview = findViewById(R.id.bottom);


    }


    /**
     * 等于就是IOS中的代理咯
     */
    class ExpendEacherOtherViewDragHelper(dragviwe: ExpendEacherOtherViewGroup) : ViewDragHelper.Callback() {

        private val dragviwe: ExpendEacherOtherViewGroup;


        init {

            this.dragviwe = dragviwe

        }

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return true
        }

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {



            when (changedView.id) {
                R.id.top -> {
                    Log.d("当前VIEW的高度","${changedView.height}");
                    //layout过后changeview的高度就已经变了
                    changedView.layout(0, 0, changedView.width, changedView.height + top)
                    Log.d("过后VIEW的高度","${changedView.height}");
                    dragviwe.greenview.layout(
                        0,
                        changedView.height,
                        dragviwe.greenview.width,
                        dragviwe.height
                    )
                    if(changedView is ViewGroup) {
                        dragviwe.setLayout(changedView, changedView.height)
                    }//改变VIEW的子view大小更新

                    if(dragviwe.greenview is ViewGroup){
                    dragviwe.setLayout(
                        dragviwe.greenview,
                        dragviwe.height - changedView.height
                    )}
                    //重新计算子VIEWGROUP中的内容
                    dragviwe.requestLayout()
                }
                R.id.bottom -> {
                    Log.d("距离上面的距离","${top}")

                    changedView.layout(
                        0,
                        top,
                        changedView.width,
                        dragviwe.height
                    )
                    if(changedView is ViewGroup) {
                        dragviwe.setLayout(changedView, dragviwe.height - top)
                    }

                    dragviwe.redview.layout(0, 0, dragviwe.redview.width, top);
                    if(dragviwe.redview is ViewGroup) {
                        dragviwe.setLayout(
                            dragviwe.redview,
                            top
                        )
                    }

                    dragviwe.requestLayout()

                }

            }


        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {

            Log.d("偏移高度", "${top},${dy}")


         return  when(child.id){

             R.id.top->{

                 return    if (child.height+top in dragviwe.MinHeight until  dragviwe.height-dragviwe.MinHeight) {


                     return top;
                 }else{

                     return 0
                 }
             }

             R.id.bottom->{
                 Log.d("距离上面的距离的top","${top}")
                 return    if (top in dragviwe.MinHeight until  dragviwe.height-dragviwe.MinHeight) {
                     return top;
                 }else if(top<dragviwe.MinHeight){
                     return dragviwe.MinHeight

                 }else if(top>dragviwe.height-dragviwe.MinHeight){

                     return dragviwe.height-dragviwe.MinHeight

                 }else{

                     return 0
                 }
             }


             else ->{

                 return 0
             }


         }

//
        }


        //子VIEW如果可以滑动，拿到滑动的范围
        override fun getViewVerticalDragRange(child: View): Int {
            return dragviwe.height-dragviwe.MinHeight;
        }


        override fun onViewDragStateChanged(state: Int) {
            super.onViewDragStateChanged(state)
            Log.d("状态","$state")
        }


        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            super.onViewReleased(releasedChild, xvel, yvel)

            //手离开的时候刷新下
            dragviwe.requestLayout()
        }

    }


}