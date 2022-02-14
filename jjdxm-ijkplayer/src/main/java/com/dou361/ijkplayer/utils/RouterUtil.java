package com.dou361.ijkplayer.utils;

/**
 * 管理页面路由路径及跳转参数
 *
 *      *      步骤：
 *      *         1.先在这个页面定义路径和参数
 *      *         2.在对应类顶部插入Route注解 如： @Route(path = RouterUtil.URL_APP_LOGIN_ACTIVITY, name = "登录页面")
 *      *                                            path，name要求必须写，name是用于javadoc生成
 *      *         3.如果有注入参数，添加Autowired注解，如： @Autowired(name = RouterUtil.PARAM_APP_LOGIN_ACTIVITY_AUTO_LOGIN_BOOLEAN, desc = "字段说明")
 *      *                                            name，desc要求必须写，desc是用于javadoc生成
 *      *         4.如果有注入参数，在页面创建的时候调用 ARouter.getInstance().inject(this);  将参数获取出来，如果没有注入参数，可不写这句话
 *
 *      *
 *      * 注解格式看步骤里的说明 @Route：path，name要求必须写  @Autowired：name，desc要求必须写
 *      *
 *      * 页面路径 命名格式 key: URL_(模块名称)_(页面名称不带后缀)_(ACTIVITY|FRAGMENT|SERVICE)
 *      *                            全大写，后缀拆开主要是凸显类型，以免有些页面命名不规范，看不出来是什么类型
 *      *
 *      *                 value: /模块名称/类名称
 *      *                            两层即可（如有重复可再加一层路径，类尽量避免同样的命名)，不用按包路径来，太复杂，
 *      *                            且在Preference--Plugins里安装ARouter Helper后，在跳转代码的行首上会有个靶心图案，点击后可跳转到对应页面，很方便
 *      *                            因此也不建议再用公共方法封装跳转代码
 *      *                            * 第一层级会默认当做分组，所以不能有多个模块里用到同个分组，
 *                                     否则因为访问一个模块的时候分组提前加载，后换了一个模块，还是这个分组，
 *                                     已存在，不会再去取该分组的路由配置，跳转的时候可能找不到对应的页面
 *      *
 *      * 参数   命名格式 PARAM_(模块名称)_(页面名称不带后缀)_(ACTIVITY|FRAGMENT|SERVICE)_(参数名称)_(参数类型)
 *      *                            key/value 原则上保持一致，类似于BUNDLE，特殊情况下可自定义value
 *      *                            命名应能体现参数作用，尽量写注释，特殊情况(如同时作为入参出参)一定需要写注释
 *
 *
 *      * 注释写清楚 页面名称 及 class路径 （虽然生成的javadoc有，但是那个分散在各个模块，还是看这个集合方便)
 *      * 生成的文档路径:各个模块下 build/generated/source/apt/(debug or release)/com/alibaba/android/arouter/docs/arouter-map-of-${moduleName}.json
 *
 *
 *      GitHub: https://github.com/alibaba/ARouter/blob/master/README_CN.md
 *
 *
 *
 *
 * 常用使用方法：
 *
 * // 构建标准的路由请求
 * ARouter.getInstance().build(RouterUtil.URL_APP_LOGIN_ACTIVITY).navigation();
 *
 * // 构建标准的路由请求，并指定分组
 * ARouter.getInstance().build(RouterUtil.URL_APP_LOGIN_ACTIVITY, "app").navigation();
 *
 * // 构建标准的路由请求，通过Uri直接解析
 * Uri uri;
 * ARouter.getInstance().build(uri).navigation();
 *
 * // 构建标准的路由请求，startActivityForResult
 * // navigation的第一个参数必须是Activity，第二个参数则是RequestCode
 * ARouter.getInstance().build(RouterUtil.URL_APP_LOGIN_ACTIVITY).navigation(this, 5);
 *
 * // 参数传递，推荐使用此方式，一个个传参列举，方便对比参数及类型是否正确
 * ARouter.getInstance().build(RouterUtil.URL_CLOCK_CLOCK_RESULT_LIST_ACTIVITY)
 *                     .withInt(RouterUtil.PARAM_CLOCK_CLOCK_RESULT_LIST_ACTIVITY_CLOCK_RESULT_LIST_TYPE_INT, ResultListEnum.TYPE_TARGET_STU_RESULT_LIST.getType())
 *                     .withLong(RouterUtil.PARAM_CLOCK_CLOCK_RESULT_LIST_ACTIVITY_TASK_ID_LONG, taskId)
 *                     .withString(RouterUtil.PARAM_CLOCK_CLOCK_RESULT_LIST_ACTIVITY_CLOCK_TASK_THEME_STRING, clockTask.getTheme())
 *                     .withString(RouterUtil.PARAM_CLOCK_CLOCK_RESULT_LIST_ACTIVITY_USER_NAME_STRING, "xxx")
 *                     .navigation();
 *
 * // 直接传递Bundle
 * Bundle params = new Bundle();
 * ARouter.getInstance()
 *     .build(RouterUtil.URL_APP_LOGIN_ACTIVITY)
 *     .with(params)
 *     .navigation();
 *
 * // 指定Flag
 * ARouter.getInstance()
 *     .build(RouterUtil.URL_APP_LOGIN_ACTIVITY)
 *     .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | etc);
 *     .navigation();
 *
 * // 获取Fragment， 强转的层级，按需越低越好
 * Fragment fragment = (Fragment) ARouter.getInstance().build(RouterUtil.URL_CIRCLE_CIRCLE_HOMEPAGE_FRAGMENT).navigation();
 *
 * // 对象传递  尽量不用，可能和原生获取参数方式不兼容，可以把对象继承serializable、parcelable等原生也支持的参数类型
 * ARouter.getInstance()
 *     .build(RouterUtil.URL_APP_LOGIN_ACTIVITY)
 *     .withObject("key", new TestObj("Jack", "Rose"))
 *     .navigation();
 *
 * // 觉得接口不够多，可以直接拿出Bundle赋值
 * ARouter.getInstance()
 *         .build("/home/main")
 *         .getExtra();
 *
 * // 转场动画(常规方式)
 * ARouter.getInstance()
 *     .build("/test/activity2")
 *     .withTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
 *     .navigation(this);
 *
 * // 转场动画(API16+)
 * ActivityOptionsCompat compat = ActivityOptionsCompat.
 *     makeScaleUpAnimation(v, v.getWidth() / 2, v.getHeight() / 2, 0, 0);
 *
 * // ps. makeSceneTransitionAnimation 使用共享元素的时候，需要在navigation方法中传入当前Activity
 *
 * ARouter.getInstance()
 *     .build("/test/activity2")
 *     .withOptionsCompat(compat)
 *     .navigation();
 *
 * // 使用绿色通道(跳过所有的拦截器)
 * ARouter.getInstance().build("/home/main").greenChannel().navigation();
 *
 *
 *  Fragment里使用startActivityForResult,因为官方框架不支持，1：通过activity分发，2：加入私人写的支持代码
 *  因为用的不多，而且fragment一般和只在本模块使用，直接跳转页不会引入其他模块，所以暂时先用老的intent跳转的方法，
 *  后期官方支持了再更新，且用intent方法，也可以用classname，不用引入其他模块
 *
 *  该框架对基本数据类型，注入能处理好代码写的默认值，但是对hashmap，arraylist等对象类型的，没有处理，有需要的，需要在注入后判断是否是null，然后初始化
 *
 * @author Luke
 * @date 2019/5/8
 */
public class RouterUtil {

    //=================================== app模块 start =======================================//
    /** 登录页面 cn.xxt.jxq.ui.login.LoginActivity */
    public final static String URL_APP_LOGIN_ACTIVITY = "/app/LoginActivity";
    public final static String PARAM_APP_LOGIN_ACTIVITY_AUTO_LOGIN_BOOLEAN = "PARAM_APP_LOGIN_ACTIVITY_AUTO_LOGIN_BOOLEAN";
    public final static String PARAM_APP_LOGIN_ACTIVITY_LOGIN_TYPE_STRING= "PARAM_APP_LOGIN_ACTIVITY_LOGIN_TYPE_STRING";


    /** 手机绑定页面 cn.xxt.jxq.ui.login.MobileBindActivity */
    public final static String URL_APP_MOBILE_BIND_ACTIVITY = "/app/MobileBindActivity";


    /** 介绍页页面 cn.xxt.jxq.ui.welcome.WelcomeActivity */
    public final static String URL_APP_WELCOME_ACTIVITY = "/app/WelcomeActivity";


    /** 介绍页页面 cn.xxt.jxq.ui.intro.IntroActivity */
    public final static String URL_APP_INTRO_ACTIVITY = "/app/IntroActivity";



    /** 首页导航Activity cn.xxt.jxq.ui.main.HomeMainActivity */
    public final static String URL_APP_HOME_MAIN_ACTIVITY = "/app/HomeMainActivity";



    /** 首页Activity cn.xxt.jxq.ui.homepage.HomePageActivity */
    public final static String URL_APP_HOME_PAGE_ACTIVITY = "/app/HomePageActivity";



    /** 首页懒加载Fragment cn.xxt.jxq.ui.homepage.LazyHomePageFragment*/
    public final static String URL_APP_LAZY_HOME_PAGE_FRAGMENT = "/app/LazyHomePageFragment";
    public final static String URL_APP_LAZY_STUDY_HOME_PAGE_FRAGMENT = "/app/LazyStudyHomePageFragment";



    /** 首页Fragment cn.xxt.jxq.ui.homepage.HomePageFragment */
    public final static String URL_APP_HOME_PAGE_FRAGMENT = "/app/HomePageFragment";

    /** 学习首页Fragment cn.xxt.jxq.ui.study.StudyHomePageFragment */
    public final static String URL_APP_STUDY_HOME_PAGE_FRAGMENT = "/app/StudyHomePageFragment";
    public final static String URL_APP_SEMESTER_FRAGMENT = "/app/SemesterFragment";
    public final static String URL_APP_ALL_SEMESTER_FRAGMENT = "/app/AllSemesterFragment";

    /** 资讯Fragment cn.xxt.jxq.ui.homepage.HomePageFragment */
    public final static String URL_APP_RESOURCES_FRAGMENT = "/app/ResourcesFragment";
    public final static String PARAM_APP_RESOURCES_FRAGMENT_NOTICE_TYPE_ID_INT = "PARAM_APP_RESOURCES_FRAGMENT_NOTICE_TYPE_ID_INT";


    /** 绑定手机成功提示activity cn.xxt.jxq.ui.login.MobileBindSuccessWithTipActivity */
    public static final String URL_APP_MOBILE_BIND_SUCCESS_WITH_TIP_ACTIVITY = "URL_APP_MOBILE_BIND_SUCCESS_WITH_TIP_ACTIVITY";
    public static final String PARAM_APP_MOBILE_BIND_SUCCESS_WITH_TIP_ACTIVITY_MOBILE_STRING = "PARAM_APP_MOBILE_BIND_SUCCESS_WITH_TIP_ACTIVITY_MOBILE_STRING";
    public static final String PARAM_APP_MOBILE_BIND_SUCCESS_WITH_TIP_ACTIVITY_LOGIN_TOKEN_STRING = "PARAM_APP_MOBILE_BIND_SUCCESS_WITH_TIP_ACTIVITY_LOGIN_TOKEN_STRING";
    //=================================== app模块 end =======================================//









    //=================================== circle模块 start =======================================//
    /** 班级圈主页页面 cn.xxt.circle.ui.activity.CircleActivity */
    public final static String URL_CIRCLE_CIRCLE_ACTIVITY = "/circle/CircleActivity";



    /** 动态详情界面 cn.xxt.circle.ui.activity.CircleDynamicDetailsActivity */
    public final static String URL_CIRCLE_CIRCLE_DYNAMIC_DETAILS_ACTIVITY = "/circle/CircleDynamicDetailsActivity";
    public final static String PARAM_CIRCLE_CIRCLE_DYNAMIC_DETAILS_ACTIVITY_DYNAMIC_ID_LONG = "PARAM_CIRCLE_CIRCLE_DYNAMIC_DETAILS_ACTIVITY_DYNAMIC_ID_LONG";
    public final static String PARAM_CIRCLE_CIRCLE_DYNAMIC_DETAILS_ACTIVITY_CREATOR_NAME_STRING = "PARAM_CIRCLE_CIRCLE_DYNAMIC_DETAILS_ACTIVITY_CREATOR_NAME_STRING";
    public final static String PARAM_CIRCLE_CIRCLE_DYNAMIC_DETAILS_ACTIVITY_MSG_IS_PRAISE_BOOLEAN = "PARAM_CIRCLE_CIRCLE_DYNAMIC_DETAILS_ACTIVITY_MSG_IS_PRAISE_BOOLEAN";
    public final static String PARAM_CIRCLE_CIRCLE_DYNAMIC_DETAILS_ACTIVITY_REPLY_COMMENT_ID_LONG = "PARAM_CIRCLE_CIRCLE_DYNAMIC_DETAILS_ACTIVITY_REPLY_COMMENT_ID_LONG";
    public final static String PARAM_CIRCLE_CIRCLE_DYNAMIC_DETAILS_ACTIVITY_CIRCLE_ID_LONG = "PARAM_CIRCLE_CIRCLE_DYNAMIC_DETAILS_ACTIVITY_CIRCLE_ID_LONG";



    /** 班级圈图片选择页面 cn.xxt.circle.ui.activity.CircleImageChooseActivity */
    public final static String URL_CIRCLE_CIRCLE_IMAGE_CHOOSE_ACTIVITY = "/circle/CircleImageChooseActivity";
    public final static String PARAM_CIRCLE_CIRCLE_IMAGE_CHOOSE_ACTIVITY_SHOW_CHECK_BOX_FLAG_BOOLEAN = "PARAM_CIRCLE_CIRCLE_IMAGE_CHOOSE_ACTIVITY_SHOW_CHECK_BOX_FLAG_BOOLEAN";
    // 这页面继承imagechoose，传参看那个页面
    /** 出参，裁切后的图片路径 */
    public final static String PARAM_CIRCLE_CIRCLE_IMAGE_CHOOSE_ACTIVITY_RESULT_CROP_IMAGE_PATH = "PARAM_CIRCLE_CIRCLE_IMAGE_CHOOSE_ACTIVITY_RESULT_CROP_IMAGE_PATH";




    /** 班级圈图片预览页面 cn.xxt.circle.ui.activity.CircleImagePreviewActivity */
    public final static String URL_CIRCLE_CIRCLE_IMAGE_PREVIEW_ACTIVITY = "/circle/CircleImagePreviewActivity";
    /** 还用于出参 */
    public final static String PARAM_CIRCLE_CIRCLE_IMAGE_PREVIEW_ACTIVITY_IMAGE_LIST = "PARAM_CIRCLE_CIRCLE_IMAGE_PREVIEW_ACTIVITY_IMAGE_LIST";
    public final static String PARAM_CIRCLE_CIRCLE_IMAGE_PREVIEW_ACTIVITY_SELECTED_INDEX_INT = "PARAM_CIRCLE_CIRCLE_IMAGE_PREVIEW_ACTIVITY_SELECTED_INDEX_INT";
    public final static String PARAM_CIRCLE_CIRCLE_IMAGE_PREVIEW_ACTIVITY_IMAGE_MODE_INT = "PARAM_CIRCLE_CIRCLE_IMAGE_PREVIEW_ACTIVITY_IMAGE_MODE_INT";
    public final static String PARAM_CIRCLE_CIRCLE_IMAGE_PREVIEW_ACTIVITY_DELETE_FLAG_BOOLEAN = "PARAM_CIRCLE_CIRCLE_IMAGE_PREVIEW_ACTIVITY_DELETE_FLAG_BOOLEAN";



    /** 班级圈消息列表页面 cn.xxt.circle.ui.activity.CircleMessageActivity */
    public final static String URL_CIRCLE_CIRCLE_MESSAGE_ACTIVITY = "/circle/CircleMessageActivity";



    /** 班级圈图片上传页面 cn.xxt.circle.ui.activity.CirclePicUploadActivity */
    public final static String URL_CIRCLE_CIRCLE_PIC_UPLOAD_ACTIVITY = "/circle/CirclePicUploadActivity";
    public final static String PARAM_CIRCLE_CIRCLE_PIC_UPLOAD_ACTIVITY_OPEN_FLAG_INT = "PARAM_CIRCLE_CIRCLE_PIC_UPLOAD_ACTIVITY_OPEN_FLAG_INT";
    public final static String PARAM_CIRCLE_CIRCLE_PIC_UPLOAD_ACTIVITY_UPLOAD_FLAG_INT = "PARAM_CIRCLE_CIRCLE_PIC_UPLOAD_ACTIVITY_UPLOAD_FLAG_INT";
    public final static String PARAM_CIRCLE_CIRCLE_PIC_UPLOAD_ACTIVITY_ALBUM_ID_INT = "PARAM_CIRCLE_CIRCLE_PIC_UPLOAD_ACTIVITY_ALBUM_ID_INT";
    public final static String PARAM_CIRCLE_CIRCLE_PIC_UPLOAD_ACTIVITY_CIRCLE_ID_LONG = "PARAM_CIRCLE_CIRCLE_PIC_UPLOAD_ACTIVITY_CIRCLE_ID_LONG";



    /** 班级圈动态发布页面 cn.xxt.circle.ui.activity.CirclePublishDynamicActivity */
    public final static String URL_CIRCLE_CIRCLE_PUBLISH_DYNAMIC_ACTIVITY = "/circle/CirclePublishDynamicActivity";
    public final static String PARAM_CIRCLE_CIRCLE_PUBLISH_DYNAMIC_ACTIVITY_CIRCLE_ID_LONG = "PARAM_CIRCLE_CIRCLE_PUBLISH_DYNAMIC_ACTIVITY_CIRCLE_ID_LONG";



    /** 班级圈设置页面 cn.xxt.circle.ui.activity.CircleSettingActivity */
    public final static String URL_CIRCLE_CIRCLE_SETTING_ACTIVITY = "/circle/CircleSettingActivity";
    public final static String PARAM_CIRCLE_CIRCLE_SETTING_ACTIVITY_CIRCLE_INFO_SERIALIZABLE = "PARAM_CIRCLE_CIRCLE_SETTING_ACTIVITY_CIRCLE_INFO_SERIALIZABLE";

    /**班级圈子资源界面（承载相册、课程表 ） cn.xxt.circle.ui.activity.CircleSubResourceActivity*/
    public final static String URL_CIRCLE_CIRCLE_SUB_RESOURCE_ACTIVITY = "/circle/CircleSubResourceActivity";
    public final static String PARAM_CIRCLE_CIRCLE_SUB_RESOURCE_ACTIVITY_UNIT_INFO_SERIALIZABLE = "PARAM_CIRCLE_CIRCLE_SUB_RESOURCE_ACTIVITY_UNIT_INFO_SERIALIZABLE";
    public final static String PARAM_CIRCLE_CIRCLE_SUB_RESOURCE_ACTIVITY_RESOURCE_TYPE_INT = "PARAM_CIRCLE_CIRCLE_SUB_RESOURCE_ACTIVITY_RESOURCE_TYPE_INT";
    public final static String PARAM_CIRCLE_CIRCLE_SUB_RESOURCE_ACTIVITY_CURRENT_UNIT_NAME_STRING = "PARAM_CIRCLE_CIRCLE_SUB_RESOURCE_ACTIVITY_CURRENT_UNIT_NAME_STRING";

    /**班级相册 cn.xxt.circle.ui.activity.CircleAlbumActivity*/
    public final static String URL_CIRCLE_CIRCLE_ALBUM_ACTIVITY = "/circle/CircleAlbumActivity";

    /**班级课程表 cn.xxt.circle.ui.activity.CircleScheduleActivity*/
    public final static String URL_CIRCLE_CIRCLE_SCHEDULE_ACTIVITY = "/circle/CircleScheduleActivity";

    /** 班级圈主页Fragment cn.xxt.circle.ui.homepage.CircleHomepageFragment */
    public final static String URL_CIRCLE_CIRCLE_HOMEPAGE_FRAGMENT = "/circle/CircleHomepageFragment";



    /** 班级圈主页懒加载Fragment cn.xxt.circle.ui.homepage.LazyCircleHomepageFragment */
    public final static String URL_CIRCLE_LAZY_CIRCLE_HOMEPAGE_FRAGMENT = "/circle/LazyCircleHomepageFragment";



    /** 班级圈相册Fragment cn.xxt.circle.ui.sourcefragment.CircleAlbumFragment */
    public final static String URL_CIRCLE_CIRCLE_ALBUM_FRAGMENT = "/circle/CircleAlbumFragment";



    /** 班级圈动态Fragment cn.xxt.circle.ui.sourcefragment.CircleDynamicFragment */
    public final static String URL_CIRCLE_CIRCLE_DYNAMIC_FRAGMENT = "/circle/CircleDynamicFragment";



    /** 班级圈课程表Fragment cn.xxt.circle.ui.sourcefragment.CircleScheduleFragment */
    public final static String URL_CIRCLE_CIRCLE_SCHEDULE_FRAGMENT = "/circle/CircleScheduleFragment";



    /** 班级圈调查问卷Fragment cn.xxt.circle.ui.sourcefragment.CircleSurveyFragment */
    public final static String URL_CIRCLE_CIRCLE_SURVEY_FRAGMENT = "/circle/CircleSurveyFragment";



    /** 班级圈调查问卷Fragment cn.xxt.circle.ui.sourcefragment.CircleVoteFragment */
    public final static String URL_CIRCLE_CIRCLE_VOTE_FRAGMENT = "/circle/CircleVoteFragment";



    /** 开通班级圈Fragment cn.xxt.circle.ui.sourcefragment.OpenCircleFragment */
    public final static String URL_CIRCLE_OPEN_CIRCLE_FRAGMENT = "/circle/OpenCircleFragment";



    /** 提醒开通班级圈Fragment cn.xxt.circle.ui.sourcefragment.RemindOpenCircleFragment */
    public final static String URL_CIRCLE_REMIND_OPEN_CIRCLE_FRAGMENT = "/circle/RemindOpenCircleFragment";

    //=================================== circle模块 end =======================================//








    //=================================== clocktask模块 start =======================================//
    /** 打卡成果列表页面 cn.xxt.clocktask.ui.clockresultList.ClockResultListActivity */
    public final static String URL_CLOCK_CLOCK_RESULT_LIST_ACTIVITY = "/clocktask/ClockResultListActivity";
    public final static String PARAM_CLOCK_CLOCK_RESULT_LIST_ACTIVITY_CLOCK_RESULT_LIST_TYPE_INT = "PARAM_CLOCK_CLOCK_RESULT_LIST_ACTIVITY_CLOCK_RESULT_LIST_TYPE_INT";
    public final static String PARAM_CLOCK_CLOCK_RESULT_LIST_ACTIVITY_CLOCK_TASK_THEME_STRING = "PARAM_CLOCK_CLOCK_RESULT_LIST_ACTIVITY_CLOCK_TASK_THEME_STRING";
    public final static String PARAM_CLOCK_CLOCK_RESULT_LIST_ACTIVITY_UNIT_ID_INT = "PARAM_CLOCK_CLOCK_RESULT_LIST_ACTIVITY_UNIT_ID_INT";
    public final static String PARAM_CLOCK_CLOCK_RESULT_LIST_ACTIVITY_UNIT_NAME_STRING = "PARAM_CLOCK_CLOCK_RESULT_LIST_ACTIVITY_UNIT_NAME_STRING";
    public final static String PARAM_CLOCK_CLOCK_RESULT_LIST_ACTIVITY_UNIT_TYPE_INT = "PARAM_CLOCK_CLOCK_RESULT_LIST_ACTIVITY_UNIT_TYPE_INT";
    public final static String PARAM_CLOCK_CLOCK_RESULT_LIST_ACTIVITY_TASK_ID_INT = "PARAM_CLOCK_CLOCK_RESULT_LIST_ACTIVITY_TASK_ID_INT";
    public final static String PARAM_CLOCK_CLOCK_RESULT_LIST_ACTIVITY_TASK_DATE_LONG = "PARAM_CLOCK_CLOCK_RESULT_LIST_ACTIVITY_TASK_DATE_LONG";
    public final static String PARAM_CLOCK_CLOCK_RESULT_LIST_ACTIVITY_USER_ID_LONG = "PARAM_CLOCK_CLOCK_RESULT_LIST_ACTIVITY_USER_ID_LONG";
    public final static String PARAM_CLOCK_CLOCK_RESULT_LIST_ACTIVITY_USER_TYPE_INT = "PARAM_CLOCK_CLOCK_RESULT_LIST_ACTIVITY_USER_TYPE_INT";
    public final static String PARAM_CLOCK_CLOCK_RESULT_LIST_ACTIVITY_USER_NAME_STRING = "PARAM_CLOCK_CLOCK_RESULT_LIST_ACTIVITY_USER_NAME_STRING";




    /** 学生查看任务详情页面 cn.xxt.clocktask.ui.detail.ClockTaskDetailStuActivity */
    public final static String URL_CLOCK_CLOCK_TASK_DETAIL_STU_ACTIVITY = "/clocktask/ClockTaskDetailStuActivity";
    public final static String PARAM_CLOCK_CLOCK_TASK_DETAIL_STU_ACTIVITY_TASK_ID_INT = "PARAM_CLOCK_CLOCK_TASK_DETAIL_STU_ACTIVITY_TASK_ID_INT";



    /** 老师查看任务详情页面 cn.xxt.clocktask.ui.detail.ClockTaskDetailTeaActivity */
    public final static String URL_CLOCK_CLOCK_TASK_DETAIL_TEA_ACTIVITY = "/clocktask/ClockTaskDetailTeaActivity";
    public final static String PARAM_CLOCK_CLOCK_TASK_DETAIL_TEA_ACTIVITY_TASK_ID_INT = "PARAM_CLOCK_CLOCK_TASK_DETAIL_TEA_ACTIVITY_TASK_ID_INT";



    /** 发布打卡任务页面 cn.xxt.clocktask.ui.taskassign.ClockTaskAssignActivity */
    public final static String URL_CLOCK_CLOCK_TASK_ASSIGN_ACTIVITY = "/clocktask/ClockTaskAssignActivity";
    public final static String PARAM_CLOCK_TASK_ASSIGN_ACTIVITY_TEMPLATE_CONTENT_STRING = "PARAM_CLOCK_TASK_ASSIGN_ACTIVITY_TEMPLATE_CONTENT_STRING";
    public final static String PARAM_CLOCK_TASK_ASSIGN_ACTIVITY_TEMPLATE_TITLE_STRING = "PARAM_CLOCK_TASK_ASSIGN_ACTIVITY_TEMPLATE_TITLE_STRING";
    public final static String PARAM_CLOCK_TASK_ASSIGN_ACTIVITY_TEMPLATE_ID_INT = "PARAM_CLOCK_TASK_ASSIGN_ACTIVITY_TEMPLATE_ID_INT";


    /** 打卡模板页面 cn.xxt.clocktask.ui.taskassign.ClockTemplateListActivity */
    public final static String URL_CLOCK_CLOCK_TEMPLATE_LIST_ACTIVITY = "/clocktask/ClockTemplateListActivity";



    /** 班级打卡成果清单页面 cn.xxt.clocktask.ui.clockresultList.ClockResultPersonnelListActivity */
    public final static String URL_CLOCK_CLOCK_RESULT_PERSONNEL_LIST_ACTIVITY = "/clocktask/ClockResultPersonnelListActivity";
    public final static String PARAM_CLOCK_CLOCK_RESULT_PERSONNEL_LIST_ACTIVITY_TITLE_STRING = "PARAM_CLOCK_CLOCK_RESULT_PERSONNEL_LIST_ACTIVITY_TITLE_STRING";
    public final static String PARAM_CLOCK_CLOCK_RESULT_PERSONNEL_LIST_ACTIVITY_THEME_STRING = "PARAM_CLOCK_CLOCK_RESULT_PERSONNEL_LIST_ACTIVITY_THEME_STRING";
    public final static String PARAM_CLOCK_CLOCK_RESULT_PERSONNEL_LIST_ACTIVITY_UNIT_ID_INT = "PARAM_CLOCK_CLOCK_RESULT_PERSONNEL_LIST_ACTIVITY_UNIT_ID_INT";
    public final static String PARAM_CLOCK_CLOCK_RESULT_PERSONNEL_LIST_ACTIVITY_UNIT_TYPE_INT = "PARAM_CLOCK_CLOCK_RESULT_PERSONNEL_LIST_ACTIVITY_UNIT_TYPE_INT";
    public final static String PARAM_CLOCK_CLOCK_RESULT_PERSONNEL_LIST_ACTIVITY_TASK_ID_INT = "PARAM_CLOCK_CLOCK_RESULT_PERSONNEL_LIST_ACTIVITY_TASK_ID_INT";
    public final static String PARAM_CLOCK_CLOCK_RESULT_PERSONNEL_LIST_ACTIVITY_TASK_DATE_LONG = "PARAM_CLOCK_CLOCK_RESULT_PERSONNEL_LIST_ACTIVITY_TASK_DATE_LONG";



    /** 打卡任务列表页面 cn.xxt.clocktask.ui.clocktasklist.ClockTaskListActivity */
    public final static String URL_CLOCK_CLOCK_TASK_LIST_ACTIVITY = "/clocktask/ClockTaskListActivity";



    /** 打卡任务列表Fragment cn.xxt.clocktask.ui.clocktasklist.ClockTaskListFragment */
    public final static String URL_CLOCK_CLOCK_TASK_LIST_FRAGMENT = "/clocktask/ClockTaskListFragment";
    public final static String PARAM_CLOCK_CLOCK_TASK_LIST_FRAGMENT_TASK_STATUS_INT = "PARAM_CLOCK_CLOCK_TASK_LIST_FRAGMENT_TASK_STATUS_INT";



    /** 提交打卡界面 cn.xxt.clocktask.ui.clocktasklist.TaskCommitActivity */
    public final static String URL_CLOCK_CLOCK_TASK_COMMIT_ACTIVITY = "/clocktask/TaskCommitActivity";
    public final static String PARAM_CLOCK_CLOCK_TASK_COMMIT_ACTIVITY_TASK_ID_INT = "PARAM_CLOCK_CLOCK_TASK_COMMIT_ACTIVITY_TASK_ID_INT";
    public final static String PARAM_CLOCK_CLOCK_TASK_COMMIT_ACTIVITY_TASK_DATE_LONG = "PARAM_CLOCK_CLOCK_TASK_COMMIT_ACTIVITY_TASK_DATE_LONG";


    /** 个人打卡记录页面 cn.xxt.clocktask.ui.detail.StuClockRecordActivity */
    public final static String URL_CLOCK_STU_CLOCK_RECORD_ACTIVITY = "/clocktask/StuClockRecordActivity";
    public final static String PARAM_CLOCK_STU_CLOCK_RECORD_ACTIVITY_TASK_ID_INT = "PARAM_CLOCK_STU_CLOCK_RECORD_ACTIVITY_TASK_ID_INT";
    public final static String PARAM_CLOCK_STU_CLOCK_RECORD_ACTIVITY_THEME_STRING = "PARAM_CLOCK_STU_CLOCK_RECORD_ACTIVITY_THEME_STRING";
    public final static String PARAM_CLOCK_STU_CLOCK_RECORD_ACTIVITY_USER_ID_LONG = "PARAM_CLOCK_STU_CLOCK_RECORD_ACTIVITY_USER_ID_LONG";
    public final static String PARAM_CLOCK_STU_CLOCK_RECORD_ACTIVITY_USER_NAME_STRING = "PARAM_CLOCK_STU_CLOCK_RECORD_ACTIVITY_USER_NAME_STRING";
    public final static String PARAM_CLOCK_STU_CLOCK_RECORD_ACTIVITY_USER_TYPE_INT = "PARAM_CLOCK_STU_CLOCK_RECORD_ACTIVITY_USER_TYPE_INT";


    /** 打卡统计页面 cn.xxt.clocktask.ui.statis.UnitStatisActivity */
    public final static String URL_CLOCK_UNIT_STATIS_ACTIVITY = "/clocktask/UnitStatisActivity";
    public final static String PARAM_CLOCK_UNIT_STATIS_ACTIVITY_TASK_ID_INT = "PARAM_CLOCK_UNIT_STATIS_ACTIVITY_TASK_ID_INT";
    public final static String PARAM_CLOCK_UNIT_STATIS_ACTIVITY_THEME_STRING = "PARAM_CLOCK_UNIT_STATIS_ACTIVITY_THEME_STRING";
    public final static String PARAM_CLOCK_UNIT_STATIS_ACTIVITY_UNIT_ID_INT = "PARAM_CLOCK_UNIT_STATIS_ACTIVITY_UNIT_ID_INT";
    public final static String PARAM_CLOCK_UNIT_STATIS_ACTIVITY_UNIT_TYPE_INT = "PARAM_CLOCK_UNIT_STATIS_ACTIVITY_UNIT_TYPE_INT";
    public final static String PARAM_CLOCK_UNIT_STATIS_ACTIVITY_UNIT_NAME_STRING = "PARAM_CLOCK_UNIT_STATIS_ACTIVITY_UNIT_NAME_STRING";
    public final static String PARAM_CLOCK_UNIT_STATIS_ACTIVITY_UNIT_LIST_SERIALIZABLE = "PARAM_CLOCK_UNIT_STATIS_ACTIVITY_UNIT_LIST_SERIALIZABLE";

    //=================================== clocktask模块 end =======================================//










    //=================================== cloud模块 start =======================================//
    /** 资源传输列表首页页面 cn.xxt.cloud.ui.download.CloudTransFileActivity */
    public final static String URL_CLOUD_CLOUD_TRANS_FILE_ACTIVITY = "/cloud/CloudTransFileActivity";



    /** 云资源单文件下载页面 cn.xxt.cloud.ui.download.FileDownloadActivity */
    public final static String URL_CLOUD_FILE_DOWNLOAD_ACTIVITY = "/cloud/FileDownloadActivity";



    /** 学校云首页 cn.xxt.cloud.ui.homepage.SchoolCloudHomePageActivity */
    public final static String URL_CLOUD_SCHOOL_CLOUD_HOME_PAGE_ACTIVITY = "/cloud/SchoolCloudHomePageActivity";



    /** 班级云首页 cn.xxt.cloud.ui.homepage.UnitCloudHomepageActivity */
    public final static String URL_CLOUD_UNIT_CLOUD_HOME_PAGE_ACTIVITY = "/cloud/UnitCloudHomepageActivity";



    /** 资源下载列表页面  cn.xxt.cloud.ui.download.CloudDownloadFragment */
    public final static String URL_CLOUD_CLOUD_DOWNLOAD_FRAGMENT = "/cloud/CloudDownloadFragment";



    /** 资源上传列表页面  cn.xxt.cloud.ui.download.CloudUploadFragment */
    public final static String URL_CLOUD_CLOUD_UPLOAD_FRAGMENT = "/cloud/CloudUploadFragment";



    /** 资源移动页面 cn.xxt.cloud.ui.homepage.MoveFileActivity */
    public final static String URL_CLOUD_MOVE_FILE_ACTIVITY = "/cloud/MoveFileActivity";
    public final static String PARAM_CLOUD_MOVE_FILE_ACTIVITY_CLOUD_ID_INT = "PARAM_CLOUD_MOVE_FILE_ACTIVITY_CLOUD_ID_INT";



    /** 全部资源页面  cn.xxt.cloud.ui.sourcefragment.AllFileFragment */
    public final static String URL_CLOUD_ALL_FILE_FRAGMENT = "/cloud/AllFileFragment";
    public final static String PARAM_CLOUD_ALL_FILE_FRAGMENT_FOLDER_ID_INT = "PARAM_CLOUD_ALL_FILE_FRAGMENT_FOLDER_ID_INT";
    public final static String PARAM_CLOUD_MOVE_FILE_ACTIVITY_FOLDER_NAME_STRING = "PARAM_CLOUD_MOVE_FILE_ACTIVITY_FOLDER_NAME_STRING";



    /** 全部资源父页面  cn.xxt.cloud.ui.sourcefragment.AllFileParentFragment */
    public final static String URL_CLOUD_ALL_FILE_PARENT_FRAGMENT = "/cloud/AllFileParentFragment";



    /** 全部资源父页面  cn.xxt.cloud.ui.sourcefragment.GeneralFileFragment */
    public final static String URL_CLOUD_GENERAL_FILE_FRAGMENT = "/cloud/GeneralFileFragment";

    //=================================== cloud模块 end =======================================//








    //=================================== common模块 start =======================================//
    /** 手写签名/画板页面 cn.xxt.commons.ui.handWritten.XXTDrawingBoardActivity */
    public static final String URL_LIBRARY_DRAWING_BOARD_ACTIVITY = "/commons/XXTDrawingBoardActivity";

    /** 通用编辑页面 cn.xxt.commons.ui.commonedit.CommonEditActivity */
    public final static String URL_COMMONS_COMMON_EDIT_ACTIVITY = "/commons/CommonEditActivity";
    public final static String PARAM_COMMONS_COMMON_EDIT_ACTIVITY_TITLE_STRING = "PARAM_COMMONS_COMMON_EDIT_ACTIVITY_TITLE_STRING";
    /** 输入框默认值入参，且也用于出参传递输入结果 */
    public final static String PARAM_COMMONS_COMMON_EDIT_ACTIVITY_VALUE_STRING = "PARAM_COMMONS_COMMON_EDIT_ACTIVITY_VALUE_STRING";
    public final static String PARAM_COMMONS_COMMON_EDIT_ACTIVITY_CAN_EMPTY_BOOLEAN = "PARAM_COMMONS_COMMON_EDIT_ACTIVITY_CAN_EMPTY_BOOLEAN";
    public final static String PARAM_COMMONS_COMMON_EDIT_ACTIVITY_MAX_LENGTH_INT = "PARAM_COMMONS_COMMON_EDIT_ACTIVITY_MAX_LENGTH_INT";



    /** 图片选择页面 cn.xxt.commons.ui.image.ImageChooseActivity */
    public final static String URL_COMMONS_IMAGE_CHOOSE_ACTIVITY = "/commons/ImageChooseActivity";
    /** 已选择的图片list，且也用于出参传递输入结果, 注意：因为图片选择和图片预览都有编辑模式，一个页面可能会调用两种，所以出参值弄成一样的，非常特殊 */
    public final static String PARAM_COMMONS_IMAGE_CHOOSE_OR_SHOW_ACTIVITY_SELECTED_IMAGE_LIST = "PARAM_COMMONS_IMAGE_CHOOSE_OR_SHOW_ACTIVITY_SELECTED_IMAGE_LIST";
    public final static String PARAM_COMMONS_IMAGE_CHOOSE_ACTIVITY_SELECTE_MAX_NUM_INT = "PARAM_COMMONS_IMAGE_CHOOSE_ACTIVITY_SELECTE_MAX_NUM_INT";
    public final static String PARAM_COMMONS_IMAGE_CHOOSE_ACTIVITY_SHOW_CHECK_BOX_FLAG_BOOLEAN = "PARAM_COMMONS_IMAGE_CHOOSE_ACTIVITY_SHOW_CHECK_BOX_FLAG_BOOLEAN";



    /** 图片选择页面 cn.xxt.commons.ui.image.ImageShowActivity */
    public final static String URL_COMMONS_IMAGE_SHOW_ACTIVITY = "/commons/ImageShowActivity";
    public final static String PARAM_COMMONS_IMAGE_SHOW_ACTIVITY_IMAGE_LIST = "PARAM_COMMONS_IMAGE_SHOW_ACTIVITY_IMAGE_LIST";
    public final static String PARAM_COMMONS_IMAGE_SHOW_ACTIVITY_SELECTED_INDEX_INT = "PARAM_COMMONS_IMAGE_SHOW_ACTIVITY_SELECTED_INDEX_INT";
    public final static String PARAM_COMMONS_IMAGE_SHOW_ACTIVITY_SHOW_SAVE_FUNC_BOOLEAN = "PARAM_COMMONS_IMAGE_SHOW_ACTIVITY_SHOW_SAVE_FUNC_BOOLEAN";
    public final static String PARAM_COMMONS_IMAGE_SHOW_ACTIVITY_SHOW_EDIT_FUNC_BOOLEAN = "PARAM_COMMONS_IMAGE_SHOW_ACTIVITY_SHOW_EDIT_FUNC_BOOLEAN";
    public final static String PARAM_COMMONS_IMAGE_SHOW_ACTIVITY_SELECTE_MAX_NUM_INT = "PARAM_COMMONS_IMAGE_SHOW_ACTIVITY_SELECTE_MAX_NUM_INT";
    public final static String PARAM_COMMONS_IMAGE_SHOW_ACTIVITY_SAVE_IMAGE_LIST_KEY_STRING = "PARAM_COMMONS_IMAGE_SHOW_ACTIVITY_SAVE_IMAGE_LIST_KEY_STRING";
    /** 出参，是否点右上角确定的 */
    public final static String PARAM_COMMONS_IMAGE_SHOW_ACTIVITY_RESULT_IS_CONFIRM_BOOLEAN = "PARAM_COMMONS_IMAGE_SHOW_ACTIVITY_RESULT_IS_CONFIRM_BOOLEAN";
    public final static String PARAM_COMMONS_IMAGE_SHOW_ACTIVITY_RESULT_EDITED_IMAGE_PATH_STRING = "PARAM_COMMONS_IMAGE_SHOW_ACTIVITY_RESULT_EDITED_IMAGE_PATH_STRING";
    public final static String PARAM_COMMONS_IMAGE_SHOW_ACTIVITY_RESULT_EDITED_IMAGE_POSITION_INT = "PARAM_COMMONS_IMAGE_SHOW_ACTIVITY_RESULT_EDITED_IMAGE_POSITION_INT";
    public final static String PARAM_COMMONS_IMAGE_SHOW_ACTIVITY_HOMEWORK_CORRECT_BOOLEAN = "PARAM_COMMONS_IMAGE_SHOW_ACTIVITY_HOMEWORK_CORRECT_BOOLEAN";


    /** 图片选择页面 cn.xxt.commons.ui.image.ImageShowNewActivity */
    public final static String URL_COMMONS_IMAGE_SHOW_NEW_ACTIVITY = "/commons/ImageShowNewActivity";
    public final static String PARAM_COMMONS_IMAGE_SHOW_NEW_ACTIVITY_IMAGE_LIST = "PARAM_COMMONS_IMAGE_SHOW_NEW_ACTIVITY_IMAGE_LIST";
    public final static String PARAM_COMMONS_IMAGE_SHOW_NEW_ACTIVITY_SELECTED_INDEX_INT = "PARAM_COMMONS_IMAGE_SHOW_NEW_ACTIVITY_SELECTED_INDEX_INT";
    public final static String PARAM_COMMONS_IMAGE_SHOW_NEW_ACTIVITY_SHOW_SAVE_FUNC_BOOLEAN = "PARAM_COMMONS_IMAGE_SHOW_NEW_ACTIVITY_SHOW_SAVE_FUNC_BOOLEAN";
    public final static String PARAM_COMMONS_IMAGE_SHOW_NEW_ACTIVITY_SHOW_EDIT_FUNC_BOOLEAN = "PARAM_COMMONS_IMAGE_SHOW_NEW_ACTIVITY_SHOW_EDIT_FUNC_BOOLEAN";
    public final static String PARAM_COMMONS_IMAGE_SHOW_NEW_ACTIVITY_EDIT_FUNC_TITLE_STRING = "PARAM_COMMONS_IMAGE_SHOW_NEW_ACTIVITY_EDIT_FUNC_TITLE_STRING";
    public final static String PARAM_COMMONS_IMAGE_SHOW_NEW_ACTIVITY_HOMEWORK_CORRECT_BOOLEAN = "PARAM_COMMONS_IMAGE_SHOW_NEW_ACTIVITY_HOMEWORK_CORRECT_BOOLEAN";

    /** 图片选择后从布置页面点击图片预览：cn.xxt.commons.ui.image.ChosenImagePreviewActivity */
    public final static String URL_COMMONS_CHOSEN_IMAGE_PREVIEW_ACTIVITY = "/commons/ChosenImagePreviewActivity";
    public final static String PARAM_COMMONS_CHOSEN_IMAGE_PREVIEW_ACTIVITY_IMAGE_LIST = "PARAM_COMMONS_CHOSEN_IMAGE_PREVIEW_ACTIVITY_IMAGE_LIST";
    public final static String PARAM_COMMONS_CHOSEN_IMAGE_PREVIEW_ACTIVITY_SELECTED_INDEX_INT = "PARAM_COMMONS_CHOSEN_IMAGE_PREVIEW_ACTIVITY_SELECTED_INDEX_INT";


    /** 朗读页面 cn.xxt.commons.ui.reading.GllReadingActivity */
    public final static String URL_COMMONS_GLL_READING_ACTIVITY = "/commons/GllReadingActivity";
    public final static String PARAM_COMMONS_GLL_READING_ACTIVITY_OPER_TYPE_INT = "PARAM_COMMONS_GLL_READING_ACTIVITY_OPER_TYPE_INT";
    public final static String PARAM_COMMONS_GLL_READING_ACTIVITY_TITLE_STRING = "PARAM_COMMONS_GLL_READING_ACTIVITY_TITLE_STRING";
    public final static String PARAM_COMMONS_GLL_READING_ACTIVITY_SUB_TITLE_STRING = "PARAM_COMMONS_GLL_READING_ACTIVITY_SUB_TITLE_STRING";
    public final static String PARAM_COMMONS_GLL_READING_ACTIVITY_CONTENT_STRING = "PARAM_COMMONS_GLL_READING_ACTIVITY_CONTENT_STRING";
    public final static String PARAM_COMMONS_GLL_READING_ACTIVITY_RECITE_CONTENT_STRING = "PARAM_COMMONS_GLL_READING_ACTIVITY_RECITE_CONTENT_STRING";
    public final static String PARAM_COMMONS_GLL_READING_ACTIVITY_ALIGN_STRING = "PARAM_COMMONS_GLL_READING_ACTIVITY_ALIGN_STRING";
    public final static String PARAM_COMMONS_GLL_READING_ACTIVITY_CHANNEL_CONFIG_INT = "PARAM_COMMONS_GLL_READING_ACTIVITY_CHANNEL_CONFIG_INT";
    public final static String PARAM_COMMONS_GLL_READING_ACTIVITY_ENCODING_CONFIG_INT = "PARAM_COMMONS_GLL_READING_ACTIVITY_ENCODING_CONFIG_INT";
    public final static String PARAM_COMMONS_GLL_READING_ACTIVITY_OUT_BIT_RATE_INT = "PARAM_COMMONS_GLL_READING_ACTIVITY_OUT_BIT_RATE_INT";
    public final static String PARAM_COMMONS_GLL_READING_ACTIVITY_RECORD_DIR_STRING = "PARAM_COMMONS_GLL_READING_ACTIVITY_RECORD_DIR_STRING";
    public final static String PARAM_COMMONS_GLL_READING_ACTIVITY_RECORD_FORMAT_STRING = "PARAM_COMMONS_GLL_READING_ACTIVITY_RECORD_FORMAT_STRING";
    public final static String PARAM_COMMONS_GLL_READING_ACTIVITY_SAMPLE_RATE_INT = "PARAM_COMMONS_GLL_READING_ACTIVITY_SAMPLE_RATE_INT";
    public final static String PARAM_COMMONS_GLL_READING_ACTIVITY_UPLOAD_AUDIO_TEXT_STRING = "PARAM_COMMONS_GLL_READING_ACTIVITY_UPLOAD_AUDIO_TEXT_STRING";
    public final static String PARAM_COMMONS_GLL_READING_ACTIVITY_MAX_DURATION_INT = "PARAM_COMMONS_GLL_READING_ACTIVITY_MAX_DURATION_INT";
    public final static String PARAM_COMMONS_GLL_READING_ACTIVITY_AUTO_DISMISS_BOOLEAN = "PARAM_COMMONS_GLL_READING_ACTIVITY_AUTO_DISMISS_BOOLEAN";

    //=================================== common模块 end =======================================//






    //=================================== hnxxt模块 start =======================================//
    /** 联系人详情页面 cn.xxt.hnxxt.ui.contact.contactdetail.ContactDetailActivity */
    public static final String URL_HNXXT_CONTACT_DETAIL_ACTIVITY = "/hnxxt/ContactDetailActivity";
    /** 联系人信息 */
    public final static String PARAM_HNXXT_CONTACT_DETAIL_ACTIVITY_CONTACT_INFO_MAP = "PARAM_HNXXT_CONTACT_DETAIL_ACTIVITY_CONTACT_INFO_MAP";



    /** 通讯录主界面 cn.xxt.hnxxt.ui.contact.contactmain.ContactHnxxtActivity */
    public static final String URL_HNXXT_CONTACT_HNXXT_ACTIVITY = "/hnxxt/ContactHnxxtActivity";



    /** 通讯录页面 cn.xxt.hnxxt.ui.contact.contactmain.ContactListFragment */
    public static final String URL_HNXXT_CONTACT_LIST_FRAGMENT = "/hnxxt/ContactListFragment";



    /** 选择联系人主activity cn.xxt.hnxxt.ui.contactchooser.HnxxtContactsChooserActivity */
    public static final String URL_HNXXT_HNXXT_CONTACTS_CHOOSER_ACTIVITY = "/hnxxt/HnxxtContactsChooserActivity";


    /** 选择联系人fragment cn.xxt.hnxxt.ui.contactchooser.HnxxtContactsChooserFragment */
    public static final String URL_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT = "/hnxxt/HnxxtContactsChooserFragment";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_FORWARD_TEXT_STRING = "PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_FORWARD_TEXT_STRING";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_BUNDLE_CONTACT_CHOOSE_WHETER_RXBUS = "PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_BUNDLE_CONTACT_CHOOSE_WHETER_RXBUS";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_BUNDLE_CONTACT_CHOOSE_FLAG_NO_UNIQUE = "PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_BUNDLE_CONTACT_CHOOSE_FLAG_NO_UNIQUE";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_SHOW_PERSON_TYPE_FLAG_INT = "showFlag";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_SELECT_SINGLE_PERSON_TYPE_FLAG_BOOLEAN = "PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_SELECT_SINGLE_PERSON_TYPE_FLAG_BOOLEAN";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_SELECTED_TYPE_STRING = "PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_SELECTED_TYPE_STRING";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_CHOOSED_LIST_SERIALIZABLE = "PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_CHOOSED_LIST_SERIALIZABLE";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_SELECTED_HIS_UNIT_TYPE_INT = "PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_SELECTED_HIS_UNIT_TYPE_INT";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_SHARE_PARAMS_MODEL_SERIALIZABLE = "PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_SHARE_PARAMS_MODEL_SERIALIZABLE";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_SHARE_TYPE_INT = "PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_SHARE_TYPE_INT";
    /** 特殊处理，因为已有天天练业务调整的话代价太大，所以key值保持原来的key值 */
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_KEY_BUSINESS_TYPE_INT = "KEY_BUSINESS_TYPE";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_APP_TYPE_INT = "appType";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_DURATION_INT = "duration";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_IS_SMS_INT = "isSms";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_IS_FEEDBACK_INT = "isFeedback";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_SEND_TIME_STRING = "sendTime";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_MSG_CONTENT_STRING = "msgContent";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_RESOURCE_DATA_STRING = "resourceData";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_SOURCE_NAME_STRING = "sourceName";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_MULTIPLE_FLAG_INT = "multipleFlag";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_APP_IDS_STRING = "appIds";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_FRAGMENT_NAME_STRING = "name";


    /** 选择联系人主activity cn.xxt.hnxxt.ui.contactchoosernew.HnxxtContactsChooserNewActivity */
    public static final String URL_HNXXT_HNXXT_CONTACTS_CHOOSER_NEW_ACTIVITY ="/hnxxt/HnxxtContactsChooserNewActivity";
    /** 选择联系人fragment cn.xxt.hnxxt.ui.contactchoosernew.HnxxtContactsChooserNewFragment */
    public static final String URL_HNXXT_HNXXT_CONTACTS_CHOOSER_NEW_FRAGMENT = "/hnxxt/HnxxtContactsChooserNewFragment";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_NEW_FRAGMENT_BUNDLE_CONTACT_CHOOSE_WHETER_RXBUS = "PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_NEW_FRAGMENT_BUNDLE_CONTACT_CHOOSE_WHETER_RXBUS";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_NEW_FRAGMENT_BUNDLE_CONTACT_CHOOSE_FLAG_NO_UNIQUE = "PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_NEW_FRAGMENT_BUNDLE_CONTACT_CHOOSE_FLAG_NO_UNIQUE";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_NEW_FRAGMENT_SHOW_PERSON_TYPE_FLAG_INT = "PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_NEW_FRAGMENT_SHOW_PERSON_TYPE_FLAG_INT";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_NEW_FRAGMENT_SELECT_SINGLE_PERSON_TYPE_FLAG_BOOLEAN = "PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_NEW_FRAGMENT_SELECT_SINGLE_PERSON_TYPE_FLAG_BOOLEAN";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_NEW_FRAGMENT_SELECTED_TYPE_INT= "PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_NEW_FRAGMENT_SELECTED_TYPE_INT";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_NEW_FRAGMENT_CHOOSED_LIST_SERIALIZABLE = "PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_NEW_FRAGMENT_CHOOSED_LIST_SERIALIZABLE";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_NEW_FRAGMENT_HIDE_NAV_BAR_BOOLEAN = "PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_NEW_FRAGMENT_HIDE_NAV_BAR_BOOLEAN";
    public static final String PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_NEW_FRAGMENT_USE_NEW_CONTACTS_BOOLEAN = "PARAM_HNXXT_HNXXT_CONTACTS_CHOOSER_NEW_FRAGMENT_USE_NEW_CONTACTS_BOOLEAN";


    /** 会话详情界面 cn.xxt.hnxxt.ui.jxlx.GroupDetailActivity */
    public static final String URL_HNXXT_GROUP_DETAIL_ACTIVITY = "/hnxxt/GroupDetailActivity";
    /** 会话组名称 */
    public static final String PARAM_HNXXT_GROUP_DETAIL_ACTIVITY_ALIAS_NAME_STRING = "PARAM_HNXXT_GROUP_DETAIL_ACTIVITY_ALIAS_NAME_STRING";
    /** 会话组id */
    public static final String PARAM_HNXXT_GROUP_DETAIL_ACTIVITY_MSG_GROUP_ID_STRING = "PARAM_HNXXT_GROUP_DETAIL_ACTIVITY_MSG_GROUP_ID_STRING";
    /** 发送对象id */
    public static final String PARAM_HNXXT_GROUP_DETAIL_ACTIVITY_DEST_ID_STRING = "PARAM_HNXXT_GROUP_DETAIL_ACTIVITY_DEST_ID_STRING";
    /** 发送对象信息 */
    public static final String PARAM_HNXXT_GROUP_DETAIL_ACTIVITY_DEST_INFO_STRING = "PARAM_HNXXT_GROUP_DETAIL_ACTIVITY_DEST_INFO_STRING";
    /** 信息类型 */
    public static final String PARAM_HNXXT_GROUP_DETAIL_ACTIVITY_MSG_TYPE_STRING = "PARAM_HNXXT_GROUP_DETAIL_ACTIVITY_MSG_TYPE_STRING";
    /** 发送对象用户类型 */
    public static final String PARAM_HNXXT_GROUP_DETAIL_ACTIVITY_DEST_USER_TYPE_STRING  = "PARAM_HNXXT_GROUP_DETAIL_ACTIVITY_DEST_USER_TYPE_STRING";



    /** 家校联系主界面 cn.xxt.hnxxt.ui.jxlx.JxlxTabBarActivity */
    public static final String URL_HNXXT_JXLX_TAB_BAR_ACTIVITY = "/hnxxt/JxlxTabBarActivity";
    /** 是否来自其他身份页面 */
    public static final String PARAM_HNXXT_JXLX_TAB_BAR_ACTIVITY_IS_FROM_OTHER_IDENTIFY_BOOLEAN = "PARAM_HNXXT_JXLX_TAB_BAR_ACTIVITY_IS_FROM_OTHER_IDENTIFY_BOOLEAN";
    /** 标题 */
    public static final String PARAM_HNXXT_JXLX_TAB_BAR_ACTIVITY_TITLE_STRING = "PARAM_HNXXT_JXLX_TAB_BAR_ACTIVITY_TITLE_STRING";
    /** webId */
    public static final String PARAM_HNXXT_JXLX_TAB_BAR_ACTIVITY_WEB_ID_INT = "PARAM_HNXXT_JXLX_TAB_BAR_ACTIVITY_WEB_ID_INT";


    /** 会话组列表界面 cn.xxt.hnxxt.ui.jxlx.MsgListFragment */
    public static final String URL_HNXXT_MSG_LIST_FRAGMENT = "/hnxxt/MsgListFragment";
    /** 是否来自其他身份页面 */
    public static final String PARAM_HNXXT_MSG_LIST_FRAGMENT_IS_FROM_OTHER_IDENTIFY_BOOLEAN = "PARAM_HNXXT_MSG_LIST_FRAGMENT_IS_FROM_OTHER_IDENTIFY_BOOLEAN";
    /** 标题 */
    public static final String PARAM_HNXXT_MSG_LIST_FRAGMENT_TITLE_STRING = "PARAM_HNXXT_MSG_LIST_FRAGMENT_TITLE_STRING";
    /** webId */
    public static final String PARAM_HNXXT_MSG_LIST_FRAGMENT_WEB_ID_INT = "PARAM_HNXXT_MSG_LIST_FRAGMENT_WEB_ID_INT";



    /** 家校联系主界面空白页 cn.xxt.hnxxt.ui.jxlx.JxlxTipFragment */
    public static final String URL_HNXXT_JXLX_TIP_FRAGMENT = "/hnxxt/JxlxTipFragment";
    public static final String PARAM_HNXXT_JXLX_TIP_FRAGMENT_SHOW_BACK_FLAG_BOOLEAN = "PARAM_HNXXT_JXLX_TIP_FRAGMENT_SHOW_BACK_FLAG_BOOLEAN";



    /** 收藏夹界面 cn.xxt.hnxxt.ui.jxlx.MsgFavoriteFragment */
    public static final String URL_HNXXT_MSG_FAVORITE_FRAGMENT = "/hnxxt/MsgFavoriteFragment";
    public static final String PARAM_HNXXT_MSG_FAVORITE_FRAGMENT_CLICK_SOURCE_INT = "PARAM_HNXXT_MSG_FAVORITE_FRAGMENT_CLICK_SOURCE_INT";



    /** 收发信息界面 cn.xxt.hnxxt.ui.jxlx.MsgReceiveAndSendActivity */
    public static final String URL_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY = "/hnxxt/MsgReceiveAndSendActivity";
    /** 页面来源 1 消息对话组列表 2 人员选择界面 */
    public static final String PARAM_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY_SOURCE_TYPE_INT = "PARAM_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY_SOURCE_TYPE_INT";
    /** 信息类型 */
    public static final String PARAM_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY_MSG_TYPE_STRING = "PARAM_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY_MSG_TYPE_STRING";
    /** 发送对象类型 */
    public static final String PARAM_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY_DEST_USER_TYPE_STRING = "PARAM_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY_DEST_USER_TYPE_STRING";
    /** 发送对象个数 */
    public static final String PARAM_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY_DEST_COUNT_STRING = "PARAM_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY_DEST_COUNT_STRING";
    /** 发送对象信息 */
    public static final String PARAM_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY_DEST_INFO_STRING = "PARAM_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY_DEST_INFO_STRING";
    /** 转发文本内容 */
    public static final String PARAM_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY_FORWARD_TEXT_STRING = "PARAM_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY_FORWARD_TEXT_STRING";
    /** 转发提示 */
    public static final String PARAM_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY_FORWARD_TIP_STRING = "PARAM_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY_FORWARD_TIP_STRING";
    /** fixedSendDate */
    public static final String PARAM_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY_FIXED_SEND_DATE_STRING = "PARAM_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY_FIXED_SEND_DATE_STRING";
    /** webId */
    public static final String PARAM_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY_WEB_ID_INT = "PARAM_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY_WEB_ID_INT";
    /** 会话组id */
    public static final String PARAM_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY_MSG_GROUP_ID_STRING  = "PARAM_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY_MSG_GROUP_ID_STRING";
    /** 发送对象id */
    public static final String PARAM_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY_DEST_ID_STRING = "PARAM_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY_DEST_ID_STRING";
    /** sourceName */
    public static final String PARAM_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY_SOURCE_NAME_STRING = "PARAM_HNXXT_MSG_RECEIVE_AND_SEND_ACTIVITY_SOURCE_NAME_STRING";


    /** 推荐模板页面 cn.xxt.hnxxt.ui.jxlx.MsgTemplateRecFragment */
    public static final String URL_HNXXT_MSG_TEMPLATE_REC_FRAGMENT = "/hnxxt/MsgTemplateRecFragment";
    public static final String PARAM_HNXXT_MSG_TEMPLATE_REC_FRAGMENT_TEMPLATE_TYPE_INT =  "PARAM_HNXXT_MSG_TEMPLATE_REC_FRAGMENT_TEMPLATE_TYPE_INT";



    /** 短信模板TabBar页面 cn.xxt.hnxxt.ui.jxlx.MsgTemplateTabHostActivity */
    public static final String URL_HNXXT_MSG_TEMPLATE_TAB_HOST_ACTIVITY = "/hnxxt/MsgTemplateTabHostActivity";
    public static final String PARAM_HNXXT_MSG_TEMPLATE_TAB_HOST_ACTIVITY_CLICK_SOURCE_INT =  "PARAM_HNXXT_MSG_TEMPLATE_TAB_HOST_ACTIVITY_CLICK_SOURCE_INT";



    /** 其它身份页面 cn.xxt.hnxxt.ui.jxlx.OtherIdentifyActivity */
    public static final String URL_HNXXT_OTHER_IDENTIFY_ACTIVITY = "/hnxxt/OtherIdentifyActivity";
    /** 是否来自其他身份页面 */
    public static final String PARAM_HNXXT_OTHER_IDENTIFY_ACTIVITY_IS_FROM_OTHER_IDENTIFY_BOOLEAN = "PARAM_HNXXT_OTHER_IDENTIFY_ACTIVITY_IS_FROM_OTHER_IDENTIFY_BOOLEAN";
    /** 标题 */
    public static final String PARAM_HNXXT_OTHER_IDENTIFY_ACTIVITY_TITLE_STRING = "PARAM_HNXXT_OTHER_IDENTIFY_ACTIVITY_TITLE_STRING";
    /** webId */
    public static final String PARAM_HNXXT_OTHER_IDENTIFY_ACTIVITY_WEB_ID_INT = "PARAM_HNXXT_OTHER_IDENTIFY_ACTIVITY_WEB_ID_INT";


    /** 推荐消息页面 cn.xxt.hnxxt.ui.jxlx.RecommendMsgListActivity */
    public static final String URL_HNXXT_RECOMMEND_MSG_LIST_ACTIVITY =  "/hnxxt/RecommendMsgListActivity";



    /** 通知详情页面 cn.xxt.hnxxt.ui.news.feedback.NewsFeedbackActivity */
    public static final String URL_HNXXT_NEWS_FEEDBACK_ACTIVITY = "/hnxxt/NewsFeedbackActivity";
    public static final String PARAM_HNXXT_NEWS_FEEDBACK_ACTIVITY_NEWS_ID_STRING =  "PARAM_HNXXT_NEWS_FEEDBACK_ACTIVITY_NEWS_ID_STRING";
    public static final String PARAM_HNXXT_NEWS_FEEDBACK_ACTIVITY_NOTICE_ID_STRING =  "PARAM_HNXXT_NEWS_FEEDBACK_ACTIVITY_NOTICE_ID_STRING";
    public static final String PARAM_HNXXT_NEWS_FEEDBACK_ACTIVITY_CREATEDATE_STRING =  "PARAM_HNXXT_NEWS_FEEDBACK_ACTIVITY_CREATEDATE_STRING";

    /** 未知消息详情页面 cn.xxt.hnxxt.ui.news.feedback.UnknownTypeNewsActivity */
    public static final String URL_HNXXT_NEWS_UNKNOWN_TYPE_NEWS_ACTIVITY = "/hnxxt/UnknownTypeNewsActivity";
    public static final String PARAM_HNXXT_NEWS_UNKNOWN_TYPE_NEWS_ACTIVITY_NEWS_DETAIL_MODEL_SERIALIZABLE = "PARAM_HNXXT_NEWS_UNKNOWN_TYPE_NEWS_ACTIVITY_NEWS_DETAIL_MODEL_SERIALIZABLE";

    /** 会话组列表懒加载页面 cn.xxt.hnxxt.ui.jxlx.LazyHnxxtMsgListFragment */
    public static final String URL_HNXXT_LAZY_HNXXT_MSG_LIST_FRAGMENT = "/hnxxt/LazyHnxxtMsgListFragment";



    /** 消息列表懒加载页面 cn.xxt.hnxxt.ui.news.newslist.LazyNewsListFragment */
    public static final String URL_HNXXT_LAZY_NEWS_LIST_FRAGMENT = "/hnxxt/LazyNewsListFragment";

    /** 通知列表懒加载页面 cn.xxt.hnxxt.ui.news.noticelist.LazyNoticeListFragment */
    public static final String URL_HNXXT_LAZY_NOTICE_LIST_FRAGMENT = "/hnxxt/LazyNoticeListFragment";

    /** 消息列表主页面 cn.xxt.hnxxt.ui.news.newslist.NewsListActivity */
    public static final String URL_HNXXT_NEWS_LIST_ACTIVITY = "/hnxxt/NewsListActivity";

    /** 通知列表主页面 cn.xxt.hnxxt.ui.news.noticelist.NoticeListActivity */
    public static final String URL_HNXXT_NOTICE_LIST_ACTIVITY = "/hnxxt/NoticeListActivity";

    /** 消息列表Fragment cn.xxt.hnxxt.ui.news.newslist.NewsListFragment */
    public static final String URL_HNXXT_NEWS_LIST_FRAGMENT = "/hnxxt/NewsListFragment";

    /** 消息列表Fragment cn.xxt.hnxxt.ui.news.noticelist.NoticeListFragment */
    public static final String URL_HNXXT_NOTICE_LIST_FRAGMENT = "/hnxxt/NoticeListFragment";


    /** 消息列表和私聊页面的父Fragment cn.xxt.hnxxt.ui.news.newslist.NewsParentFragment */
    public static final String URL_HNXXT_NEWS_PARENT_FRAGMENT = "/hnxxt/NewsParentFragment";


    /** 定时消息列表页面 cn.xxt.hnxxt.ui.news.newslist.TimingNewsListActivity */
    public static final String URL_HNXXT_TIMING_NEWS_LIST_ACTIVITY = "/hnxxt/TimingNewsListActivity";



    /** 通知发送页面 cn.xxt.hnxxt.ui.news.send.NewsSendActivity */
    public static final String URL_HNXXT_NEWS_SEND_ACTIVITY = "/hnxxt/NewsSendActivity";
    public final static String PARAM_HNXXT_NEWS_SEND_ACTIVITY_TEMPLATE_CONTENT_STRING = "PARAM_HNXXT_NEWS_SEND_ACTIVITY_TEMPLATE_CONTENT_STRING";
    public final static String PARAM_HNXXT_NEWS_SEND_ACTIVITY_TEMPLATE_ID_INT = "PARAM_HNXXT_NEWS_SEND_ACTIVITY_TEMPLATE_ID_INT";



    /** 新版布置天天练Fragment cn.xxt.hnxxt.ui.newttl.TTLAssignFragment */
    public static final String URL_HNXXT_TTL_ASSIGN_FRAGMENT = "/hnxxt/TTLAssignFragment";



    /** 新版布置天天练页面 cn.xxt.hnxxt.ui.newttl.TtlMainActivityNew */
    public static final String URL_HNXXT_TTL_MAIN_ACTIVITY_NEW = "/hnxxt/TtlMainActivityNew";



    /** 班级管理页面 cn.xxt.hnxxt.ui.unitmanager.UnitManagementActivity */
    public static final String URL_HNXXT_UNIT_MANAGEMENT_ACTIVITY = "/hnxxt/UnitManagementActivity";
    public final static String PARAM_HNXXT_UNIT_MANAGEMENT_ACTIVITY_UNLOCK_FLAG_BOOLEAN = "PARAM_HNXXT_UNIT_MANAGEMENT_ACTIVITY_UNLOCK_FLAG_BOOLEAN";



    /** 班级管理Fragment cn.xxt.hnxxt.ui.unitmanager.UnitManagementFragment */
    public static final String URL_HNXXT_UNIT_MANAGEMENT_FRAGMENT = "/hnxxt/UnitManagementFragment";
    public final static String PARAM_HNXXT_UNIT_MANAGEMENT_FRAGMENT_UNLOCK_FLAG_BOOLEAN = "PARAM_HNXXT_UNIT_MANAGEMENT_FRAGMENT_UNLOCK_FLAG_BOOLEAN";



    /** 班级管理页面 cn.xxt.hnxxt.ui.unitmanager.UnitStuOperateActivity */
    public static final String URL_HNXXT_UNIT_STU_OPERATE_ACTIVITY = "/hnxxt/UnitStuOperateActivity";
    public static final String PARAM_HNXXT_UNIT_STU_OPERATE_ACTIVITY_STU_INFO_SERIALIZABLE = "PARAM_HNXXT_UNIT_STU_OPERATE_ACTIVITY_STU_INFO_SERIALIZABLE";




    /** 加班首页 cn.xxt.hnxxt.ui.unit.JoinUnitActivity */
    public static final String URL_HNXXT_JOIN_UNIT_ACTIVITY = "/hnxxt/JoinUnitActivity";

    /** 开通校讯通页面 cn.xxt.hnxxt.ui.unit.OpenXXTActivity */
    public static final String URL_HNXXT_OPEN_XXT_ACTIVITY = "/hnxxt/OpenXXTActivity";
    public static final String PARAM_HNXXT_OPEN_XXT_ACTIVITY_STUDENT_UNIT_LIST_SERIALIZABLE = "PARAM_HNXXT_OPEN_XXT_ACTIVITY_STUDENT_UNIT_LIST_SERIALIZABLE";
    public static final String PARAM_HNXXT_OPEN_XXT_ACTIVITY_ORDER_FLAG_BOOLEAN = "PARAM_HNXXT_OPEN_XXT_ACTIVITY_ORDER_FLAG_BOOLEAN";
    public static final String PARAM_HNXXT_OPEN_XXT_ACTIVITY_TITLE_STRING = "PARAM_HNXXT_OPEN_XXT_ACTIVITY_TITLE_STRING";
    public static final String PARAM_HNXXT_OPEN_XXT_ACTIVITY_NEED_CHOOSE_IDENTITY_BOOLEAN = "PARAM_HNXXT_OPEN_XXT_ACTIVITY_NEED_CHOOSE_IDENTITY_BOOLEAN";

    /** 新加班级主页面 cn.xxt.hnxxt.ui.unit.AddUnitMainFragment */
    public static final String URL_HNXXT_ADD_UNIT_MAIN_FRAGMENT = "/hnxxt/AddUnitMainFragment";
    public static final String PARAM_HNXXT_ADD_UNIT_MAIN_FRAGMENT_OPERTYPE = "PARAM_HNXXT_ADD_UNIT_MAIN_FRAGMENT_OPERTYPE";
    public static final String PARAM_HNXXT_ADD_UNIT_MAIN_FRAGMENT_ADDED_UNIT_MODELS = "PARAM_HNXXT_ADD_UNIT_MAIN_FRAGMENT_ADDED_UNIT_MODELS";
    public static final String PARAM_HNXXT_ADD_UNIT_MAIN_FRAGMENT_MODIFY_UNIT_MODEL = "PARAM_HNXXT_ADD_UNIT_MAIN_FRAGMENT_MODIFY_UNIT_MODEL";

    /** 新加班级页面 cn.xxt.hnxxt.ui.unit.AddUnitFragment */
    public static final String URL_HNXXT_ADD_UNIT_FRAGMENT = "/hnxxt/AddUnitFragment";
    public static final String PARAM_HNXXT_ADD_UNIT_FRAGMENT_ADDED_UNIT_MODELS = "PARAM_HNXXT_ADD_UNIT_FRAGMENT_ADDED_UNIT_MODELS";

    /** 修改班级页面 cn.xxt.hnxxt.ui.unit.ModifyUnitFragment */
    public static final String URL_HNXXT_MODIFY_UNIT_FRAGMENT = "/hnxxt/ModifyUnitFragment";
    public static final String PARAM_HNXXT_MODIFY_UNIT_FRAGMENT_SELECTED_MODEL = "PARAM_HNXXT_MODIFY_UNIT_FRAGMENT_SELECTED_UNIT_MODEL";

    //=================================== hnxxt模块 end =======================================//



    //=================================== homemodule 模块 start =======================================//
    /** cn.xxt.homemodule.ui.rapid.RapidActivity */
    public static final String URL_HOME_MODULE_RAPID_ACTIVITY = "/homemodule/RapidActivity";
    public static final String PARAM_HOME_MODULE_RAPID_ACTIVITY_ROUTE_STRING = "route";

    /** cn.xxt.homemodule.ui.rapid.OralArithmeticActivity */
    public static final String URL_HOME_MODULE_ORAL_ARITHMETIC_ACTIVITY = "/homemodule/OralArithmeticActivity";
    public static final String PARAM_HOME_MODULE_ORAL_ARITHMETIC_ACTIVITY_ROUTE_STRING = "route";

    //=================================== homemodule 模块 end =======================================//


    //=================================== homework模块 start =======================================//

    /** 上传作业页面 cn.xxt.homework.ui.commit.HomeworkCommitActivity */
    public static final String URL_HOMEWORK_HOMEWORK_COMMIT_ACTIVITY= "/homework/HomeworkCommitActivity";
    public static final String PARAM_HOMEWORK_HOMEWORK_COMMIT_ACTIVITY_WID_INT = "PARAM_HOMEWORK_HOMEWORK_COMMIT_ACTIVITY_WID_INT";
    public static final String PARAM_HOMEWORK_HOMEWORK_COMMIT_ACTIVITY_RECORD_RECORD = "PARAM_HOMEWORK_HOMEWORK_COMMIT_ACTIVITY_RECORD_RECORD";


    /** 多个作业详情页面 cn.xxt.homework.ui.detail.HomeworkListDetailActivity */
    public static final String URL_HOMEWORK_HOMEWORK_LIST_DETAIL_ACTIVITY = "/homework/HomeworkListDetailActivity";
    public static final String PARAM_HOMEWORK_HOMEWORK_LIST_DETAIL_ACTIVITY_WID_INT = "PARAM_HOMEWORK_HOMEWORK_LIST_DETAIL_ACTIVITY_WID_INT";
    public static final String PARAM_HOMEWORK_HOMEWORK_LIST_DETAIL_ACTIVITY_WBA_TYPE_INT = "PARAM_HOMEWORK_HOMEWORK_LIST_DETAIL_ACTIVITY_WBA_TYPE_INT";
    public static final String PARAM_HOMEWORK_HOMEWORK_LIST_DETAIL_ACTIVITY_CURRENT_SEQ_INT = "PARAM_HOMEWORK_HOMEWORK_LIST_DETAIL_ACTIVITY_CURRENT_SEQ_INT";
    public static final String PARAM_HOMEWORK_HOMEWORK_LIST_DETAIL_ACTIVITY_HOMEWORK_TITLE_STRING = "PARAM_HOMEWORK_HOMEWORK_LIST_DETAIL_ACTIVITY_HOMEWORK_TITLE_STRING";
    public static final String PARAM_HOMEWORK_HOMEWORK_LIST_DETAIL_ACTIVITY_UNIT_ID_STRING = "PARAM_HOMEWORK_HOMEWORK_LIST_DETAIL_ACTIVITY_UNIT_ID_STRING";
    public static final String PARAM_HOMEWORK_HOMEWORK_LIST_DETAIL_ACTIVITY_STU_FINISH_INFO_LIST_PARCELABLE = "PARAM_HOMEWORK_HOMEWORK_LIST_DETAIL_ACTIVITY_STU_FINISH_INFO_LIST_PARCELABLE";
    public static final String PARAM_HOMEWORK_HOMEWORK_LIST_DETAIL_ACTIVITY_IS_FROM_EXCELLENT_BOOLEAN = "PARAM_HOMEWORK_HOMEWORK_LIST_DETAIL_ACTIVITY_IS_FROM_EXCELLENT_BOOLEAN";


    /** 多个作业详情页面 cn.xxt.homework.ui.detail.MyHomeworkActivity */
    public static final String URL_HOMEWORK_MY_HOMEWORK_ACTIVITY = "/homework/MyHomeworkActivity";
    public static final String PARAM_HOMEWORK_MY_HOMEWORK_ACTIVITY_WID_INT = "PARAM_HOMEWORK_MY_HOMEWORK_ACTIVITY_WID_INT";
    public static final String PARAM_HOMEWORK_MY_HOMEWORK_ACTIVITY_FEEDBACK_TYPE_INT = "PARAM_HOMEWORK_MY_HOMEWORK_ACTIVITY_FEEDBACK_TYPE_INT";
    public static final String PARAM_HOMEWORK_MY_HOMEWORK_ACTIVITY_WBA_TYPE_INT = "PARAM_HOMEWORK_MY_HOMEWORK_ACTIVITY_WBA_TYPE_INT";



    /** 新版作业列表主页 cn.xxt.homework.ui.mainlist.HomeworkListActivity */
    public static final String URL_HOMEWORK_HOMEWORK_LIST_ACTIVITY = "/homework/HomeworkListActivity";

    /** 新版作业列表主页 cn.xxt.homework.ui.mainlist.HomeworkListFragment */
    public static final String URL_HOMEWORK_HOMEWORK_LIST_FRAGMENT = "/homework/HomeworkListFragment";
    public static final String PARAM_HOMEWORK_HOMEWORK_LIST_FRAGMENT_SHOW_NAV_BAR_BOOLEAN = "PARAM_HOMEWORK_HOMEWORK_LIST_FRAGMENT_SHOW_NAV_BAR_BOOLEAN";

    /** 定时作业列表页面 cn.xxt.homework.ui.mainlist.TimerHomeworkListActivity */
    public static final String URL_HOMEWORK_TIMER_HOMEWORK_LIST_ACTIVITY = "/homework/TimerHomeworkListActivity";


    /** 选择作业类型页面 cn.xxt.homework.ui.newassign.ChooseHomeworkTypeActivity */
    public static final String URL_HOMEWORK_CHOOSE_HOMEWORK_TYPE_ACTIVITY = "/homework/ChooseHomeworkTypeActivity";

    /** 布置朗读作业页面 cn.xxt.homework.ui.newassign.AssignTextReadingActivity */
    public static final String URL_HOMEWORK_ASSIGN_TEXT_READING_ACTIVITY = "/homework/AssignTextReadingActivity";

    /** 布置朗读作业fragment cn.xxt.homework.ui.newassign.AssignTextReadingFragment */
    public static final String URL_HOMEWORK_ASSIGN_TEXT_READING_FRAGMENT = "/homework/AssignTextReadingFragment";

    /** 布置同步学作业页面 cn.xxt.homework.ui.newassign.AssignTbxActivity */
    public static final String URL_HOMEWORK_ASSIGN_TBX_ACTIVITY = "/homework/AssignTbxActivity";

    /** 布置同步学作业fragment cn.xxt.homework.ui.newassign.AssignTbxFragment */
    public static final String URL_HOMEWORK_ASSIGN_TBX_FRAGMENT = "/homework/AssignTbxFragment";

    /** 布置作业页面 cn.xxt.homework.ui.newassign.NewHomeworkAssignActivity */
    public static final String URL_HOMEWORK_NEW_HOMEWORK_ASSIGN_ACTIVITY = "/homework/NewHomeworkAssignActivity";



    /** 作业通报页面 cn.xxt.homework.ui.notify.HomeworkNotifyNewActivity */
    public static final String URL_HOMEWORK_HOMEWORK_NOTIFY_NEW_ACTIVITY = "/homework/HomeworkNotifyNewActivity";
    public static final String PARAM_HOMEWORK_HOMEWORK_NOTIFY_NEW_ACTIVITY_UNIT_ID_INT = "PARAM_HOMEWORK_HOMEWORK_NOTIFY_NEW_ACTIVITY_UNIT_ID_INT";
    public static final String PARAM_HOMEWORK_HOMEWORK_NOTIFY_NEW_ACTIVITY_UNIT_NAME_STRING = "PARAM_HOMEWORK_HOMEWORK_NOTIFY_NEW_ACTIVITY_UNIT_NAME_STRING";
    public static final String PARAM_HOMEWORK_HOMEWORK_NOTIFY_NEW_ACTIVITY_HOMEWORK_TITLE_STRING = "PARAM_HOMEWORK_HOMEWORK_NOTIFY_NEW_ACTIVITY_HOMEWORK_TITLE_STRING";
    public static final String PARAM_HOMEWORK_HOMEWORK_NOTIFY_NEW_ACTIVITY_FINISH_LIST_PARCELABLE = "PARAM_HOMEWORK_HOMEWORK_NOTIFY_NEW_ACTIVITY_FINISH_LIST__PARCELABLE";
    public static final String PARAM_HOMEWORK_HOMEWORK_NOTIFY_NEW_ACTIVITY_UNFINISH_LIST_PARCELABLE = "PARAM_HOMEWORK_HOMEWORK_NOTIFY_NEW_ACTIVITY_UNFINISH_LIST_PARCELABLE";
    public static final String PARAM_HOMEWORK_HOMEWORK_NOTIFY_NEW_ACTIVITY_EXCELLENT_LIST_PARCELABLE = "PARAM_HOMEWORK_HOMEWORK_NOTIFY_NEW_ACTIVITY_EXCELLENT_LIST_PARCELABLE";



    /** 作业报告页面 cn.xxt.homework.ui.report.ReportActivity */
    public static final String URL_HOMEWORK_REPORT_ACTIVITY = "/homework/ReportActivity";
    public static final String PARAM_HOMEWORK_REPORT_ACTIVITY_WID_STRING = "PARAM_HOMEWORK_REPORT_ACTIVITY_WID_STRING";

    //=================================== homework模块 end =======================================//



    //=================================== jxhd模块 start =======================================//

    /** 联系人详情界面 cn.xxt.jxhd.ui.contact.contactdetail.ContactDetailActivity */
    public static final String URL_JXHD_CONTACT_DETAIL_ACTIVITY = "/jxhd/ContactDetailActivity";
    public static final String PARAM_JXHD_CONTACT_DETAIL_ACTIVITY_CONTACT_TYPE_IS_TEACHER_BOOLEAN = "PARAM_JXHD_CONTACT_DETAIL_ACTIVITY_CONTACT_TYPE_IS_TEACHER_BOOLEAN";
    public static final String PARAM_JXHD_CONTACT_DETAIL_ACTIVITY_UNIT_ID_LONG = "PARAM_JXHD_CONTACT_DETAIL_ACTIVITY_UNIT_ID_LONG";
    public static final String PARAM_JXHD_CONTACT_DETAIL_ACTIVITY_USER_ID_LONG = "PARAM_JXHD_CONTACT_DETAIL_ACTIVITY_USER_ID_LONG";
    public static final String PARAM_JXHD_CONTACT_DETAIL_ACTIVITY_NAME_STRING = "PARAM_JXHD_CONTACT_DETAIL_ACTIVITY_NAME_STRING";

    /** 通讯录界面 cn.xxt.jxhd.ui.contact.contactlist.ContactListActivity */
    public static final String URL_JXHD_CONTACT_LIST_ACTIVITY = "/jxhd/ContactListActivity";

    /** 通讯录界面 cn.xxt.jxhd.ui.contact.contactlist.ContactListFragment */
    public static final String URL_JXHD_CONTACT_LIST_FRAGMENT = "/jxhd/ContactListFragment";

    /** 选择联系人界面 cn.xxt.jxhd.ui.contactschooser.ContactsChooserActivity */
    public static final String URL_JXHD_CONTACTS_CHOOSER_ACTIVITY = "/jxhd/ContactsChooserActivity";
    public static final String PARAM_JXHD_CONTACTS_CHOOSER_ACTIVITY_FORWARD_CONTENT_STRING = "PARAM_JXHD_CONTACTS_CHOOSER_ACTIVITY_FORWARD_CONTENT_STRING";
    public static final String PARAM_JXHD_CONTACTS_CHOOSER_ACTIVITY_FORWARD_FILE_PATH_STRING = "PARAM_JXHD_CONTACTS_CHOOSER_ACTIVITY_FORWARD_FILE_PATH_STRING";

    /** 空白页 cn.xxt.jxhd.ui.jxhdmain.BlankFragment */
    public static final String URL_JXHD_BLANK_FRAGMENT = "/jxhd/BlankFragment";

    /** 管理fragment cn.xxt.jxhd.ui.management.ManagerFragment */
    public static final String URL_JXHD_MANAGER_FRAGMENT = "/jxhd/ManagerFragment";

    /** 所有会话成员页面 cn.xxt.jxhd.ui.msglist.AllGroupMemberActivity */
    public static final String URL_JXHD_ALL_GROUP_MEMBER_ACTIVITY = "/jxhd/AllGroupMemberActivity";
    public static final String PARAM_JXHD_ALL_GROUP_MEMBER_ACTIVITY_GROUP_ID_LONG = "PARAM_JXHD_ALL_GROUP_MEMBER_ACTIVITY_GROUP_ID_LONG";

    /** 会话成员页面 cn.xxt.jxhd.ui.msglist.GroupMemberActivity */
    public static final String URL_JXHD_GROUP_MEMBER_ACTIVITY = "/jxhd/GroupMemberActivity";
    public static final String PARAM_JXHD_GROUP_MEMBER_ACTIVITY_GROUP_ID_LONG = "PARAM_JXHD_GROUP_MEMBER_ACTIVITY_GROUP_ID_LONG";

    /** 家校互动懒加载的会话组列表页面 cn.xxt.jxhd.ui.msglist.LazyJxhdMsgListFragment */
    public static final String URL_JXHD_LAZY_JXHD_MSG_LIST_FRAGMENT = "/jxhd/LazyJxhdMsgListFragment";

    /** 会话列表fragment cn.xxt.jxhd.ui.msglist.MsgGroupListFragment */
    public static final String URL_JXHD_MSG_GROUP_LIST_FRAGMENT = "/jxhd/MsgGroupListFragment";

    /** 收发信息页面 cn.xxt.jxhd.ui.msglist.MsgReceiveAndSendJxhdActivity */
    public static final String URL_JXHD_MSG_RECEIVE_AND_SEND_JXHD_ACTIVITY = "/jxhd/MsgReceiveAndSendJxhdActivity";
    public static final String PARAM_JXHD_MSG_RECEIVE_AND_SEND_JXHD_ACTIVITY_GROUP_ID_LONG = "PARAM_JXHD_MSG_RECEIVE_AND_SEND_JXHD_ACTIVITY_GROUP_ID_LONG";
    public static final String PARAM_JXHD_MSG_RECEIVE_AND_SEND_JXHD_ACTIVITY_FORWARD_CONTENT_STRING = "PARAM_JXHD_MSG_RECEIVE_AND_SEND_JXHD_ACTIVITY_FORWARD_CONTENT_STRING";
    public static final String PARAM_JXHD_MSG_RECEIVE_AND_SEND_JXHD_ACTIVITY_FORWARD_MSG_STRING = "PARAM_JXHD_MSG_RECEIVE_AND_SEND_JXHD_ACTIVITY_FORWARD_MSG_STRING";
    public static final String PARAM_JXHD_MSG_RECEIVE_AND_SEND_JXHD_ACTIVITY_FORWARD_FILE_PATH_STRING = "PARAM_JXHD_MSG_RECEIVE_AND_SEND_JXHD_ACTIVITY_FORWARD_FILE_PATH_STRING";

    /** 班级管理页面 cn.xxt.jxhd.ui.unitmanager.classesmanagement.ClassManagementActivity */
    public static final String URL_JXHD_CLASS_MANAGEMENT_ACTIVITY = "/jxhd/ClassManagementActivity";

    /** 学生信息编辑页面 cn.xxt.jxhd.ui.unitmanager.classmember.student.StudentInfoAddAndEditActivity */
    public static final String URL_JXHD_STUDENT_INFO_ADD_AND_EDIT_ACTIVITY = "/jxhd/StudentInfoAddAndEditActivity";
    public static final String PARAM_JXHD_STUDENT_INFO_ADD_AND_EDIT_ACTIVITY_FROM_FLAG_INT = "PARAM_JXHD_STUDENT_INFO_ADD_AND_EDIT_ACTIVITY_FROM_FLAG_INT";
    public static final String PARAM_JXHD_STUDENT_INFO_ADD_AND_EDIT_ACTIVITY_GROUP_ID_LONG = "PARAM_JXHD_STUDENT_INFO_ADD_AND_EDIT_ACTIVITY_GROUP_ID_LONG";
    public static final String PARAM_JXHD_STUDENT_INFO_ADD_AND_EDIT_ACTIVITY_STU_ID_LONG = "PARAM_JXHD_STUDENT_INFO_ADD_AND_EDIT_ACTIVITY_STU_ID_LONG";
    public static final String PARAM_JXHD_STUDENT_INFO_ADD_AND_EDIT_ACTIVITY_FEATURE_ID_LONG = "PARAM_JXHD_STUDENT_INFO_ADD_AND_EDIT_ACTIVITY_FEATURE_ID_LONG";
    public static final String PARAM_JXHD_STUDENT_INFO_ADD_AND_EDIT_ACTIVITY_NAME_STRING = "PARAM_JXHD_STUDENT_INFO_ADD_AND_EDIT_ACTIVITY_NAME_STRING";
    public static final String PARAM_JXHD_STUDENT_INFO_ADD_AND_EDIT_ACTIVITY_GENDER_STRING = "PARAM_JXHD_STUDENT_INFO_ADD_AND_EDIT_ACTIVITY_GENDER_STRING";
    public static final String PARAM_JXHD_STUDENT_INFO_ADD_AND_EDIT_ACTIVITY_PET_NAME_STRING = "PARAM_JXHD_STUDENT_INFO_ADD_AND_EDIT_ACTIVITY_PET_NAME_STRING";
    public static final String PARAM_JXHD_STUDENT_INFO_ADD_AND_EDIT_ACTIVITY_BIRTH_STRING = "PARAM_JXHD_STUDENT_INFO_ADD_AND_EDIT_ACTIVITY_BIRTH_STRING";
    public static final String PARAM_JXHD_STUDENT_INFO_ADD_AND_EDIT_ACTIVITY_REL_ID_INT = "PARAM_JXHD_STUDENT_INFO_ADD_AND_EDIT_ACTIVITY_REL_ID_INT";
    public static final String PARAM_JXHD_STUDENT_INFO_ADD_AND_EDIT_ACTIVITY_REL_NAME_STRING = "PARAM_JXHD_STUDENT_INFO_ADD_AND_EDIT_ACTIVITY_REL_NAME_STRING";
    public static final String PARAM_JXHD_STUDENT_INFO_ADD_AND_EDIT_ACTIVITY_MOBILE_STRING = "PARAM_JXHD_STUDENT_INFO_ADD_AND_EDIT_ACTIVITY_MOBILE_STRING";

    /** 学生信息展示页面 cn.xxt.jxhd.ui.unitmanager.classmember.student.StudentInfoShowActivity */
    public static final String URL_JXHD_STUDENT_INFO_SHOW_ACTIVITY = "/jxhd/StudentInfoShowActivity";
    public static final String PARAM_JXHD_STUDENT_INFO_SHOW_ACTIVITY_GROUP_ID_LONG = "PARAM_JXHD_STUDENT_INFO_SHOW_ACTIVITY_GROUP_ID_LONG";
    public static final String PARAM_JXHD_STUDENT_INFO_SHOW_ACTIVITY_STU_ID_LONG = "PARAM_JXHD_STUDENT_INFO_SHOW_ACTIVITY_STU_ID_LONG";
    public static final String PARAM_JXHD_STUDENT_INFO_SHOW_ACTIVITY_STU_NAME_STRING = "PARAM_JXHD_STUDENT_INFO_SHOW_ACTIVITY_STU_NAME_STRING";

    /** 学生列表 cn.xxt.jxhd.ui.unitmanager.classmember.students.StudentsListActivity */
    public static final String URL_JXHD_STUDENTS_LIST_ACTIVITY = "/jxhd/StudentsListActivity";
    public static final String PARAM_JXHD_STUDENTS_LIST_ACTIVITY_UNIT_ID_LONG = "PARAM_JXHD_STUDENTS_LIST_ACTIVITY_UNIT_ID_LONG";
    public static final String PARAM_JXHD_STUDENTS_LIST_ACTIVITY_IS_DELETE_MODE_BOOLEAN = "PARAM_JXHD_STUDENTS_LIST_ACTIVITY_IS_DELETE_MODE_BOOLEAN";

    /** 班级成员管理页面 cn.xxt.jxhd.ui.unitmanager.classmember.students.StudentsManagerActivtiy */
    public static final String URL_JXHD_STUDENTS_MANAGER_ACTIVITY = "/jxhd/StudentsManagerActivtiy";
    public static final String PARAM_JXHD_STUDENTS_MANAGER_ACTIVITY_GROUP_ID_LONG = "PARAM_JXHD_STUDENTS_MANAGER_ACTIVITY_GROUP_ID_LONG";

    /** 设置班级昵称 cn.xxt.jxhd.ui.unitmanager.classnamechange.ClassNameChangeActivity */
    public static final String URL_JXHD_CLASS_NAME_CHANGE_ACTIVITY = "/jxhd/ClassNameChangeActivity";
    public static final String PARAM_JXHD_CLASS_NAME_CHANGE_ACTIVITY_UNIT_ID_LONG = "PARAM_JXHD_CLASS_NAME_CHANGE_ACTIVITY_UNIT_ID_LONG";
    public static final String PARAM_JXHD_CLASS_NAME_CHANGE_ACTIVITY_TITLE_STRING = "PARAM_JXHD_CLASS_NAME_CHANGE_ACTIVITY_TITLE_STRING";
    public static final String PARAM_JXHD_CLASS_NAME_CHANGE_ACTIVITY_ORIGINAL_CLASS_NAME_STRING = "PARAM_JXHD_CLASS_NAME_CHANGE_ACTIVITY_ORIGINAL_CLASS_NAME_STRING";

    /** 开通短信功能页面 cn.xxt.jxhd.ui.unitmanager.smsserviceopen.SmsServiceOpenActivity */
    public static final String URL_JXHD_SMS_SERVICE_OPEN_ACTIVITY = "/jxhd/SmsServiceOpenActivity";
    public static final String PARAM_JXHD_SMS_SERVICE_OPEN_ACTIVITY_GROUP_ID_LONG = "PARAM_JXHD_SMS_SERVICE_OPEN_ACTIVITY_GROUP_ID_LONG";
    public static final String PARAM_JXHD_SMS_SERVICE_OPEN_ACTIVITY_CLASS_NAME_STRING = "PARAM_JXHD_SMS_SERVICE_OPEN_ACTIVITY_CLASS_NAME_STRING";

    /** 添加教师界面 cn.xxt.jxhd.ui.unitmanager.teacher.additionbysubject.TeacherAdditionBySubjectActivity */
    public static final String URL_JXHD_TEACHER_ADDITION_BY_SUBJECT_ACTIVITY = "/jxhd/TeacherAdditionBySubjectActivity";
    public static final String PARAM_JXHD_TEACHER_ADDITION_BY_SUBJECT_ACTIVITY_GROUP_ID_LONG = "PARAM_JXHD_TEACHER_ADDITION_BY_SUBJECT_ACTIVITY_GROUP_ID_LONG";

    /** 选择老师页面 cn.xxt.jxhd.ui.unitmanager.teacher.chooser.TeacherChooserActivity */
    public static final String URL_JXHD_TEACHER_CHOOSER_ACTIVITY = "/jxhd/TeacherChooserActivity";
    public static final String PARAM_JXHD_TEACHER_CHOOSER_ACTIVITY_CHOOSED_TEACHER_ID_LONG = "PARAM_JXHD_TEACHER_CHOOSER_ACTIVITY_CHOOSED_TEACHER_ID_LONG";
    public static final String PARAM_JXHD_TEACHER_CHOOSER_ACTIVITY_PARENT_ACTIVITY_FLAG_INT = "PARAM_JXHD_TEACHER_CHOOSER_ACTIVITY_PARENT_ACTIVITY_FLAG_INT";
    public static final String PARAM_JXHD_TEACHER_CHOOSER_ACTIVITY_CHOOSED_SUBJECT_ID_INT = "PARAM_JXHD_TEACHER_CHOOSER_ACTIVITY_CHOOSED_SUBJECT_ID_INT";
    public static final String PARAM_JXHD_TEACHER_CHOOSER_ACTIVITY_UNIT_ID_LONG = "PARAM_JXHD_TEACHER_CHOOSER_ACTIVITY_UNIT_ID_LONG";

    /** 任课老师设置界面 cn.xxt.jxhd.ui.unitmanager.teacher.main.TeacherSettingActivity */
    public static final String URL_JXHD_TEACHER_SETTING_ACTIVITY = "/jxhd/TeacherSettingActivity";
    public static final String PARAM_JXHD_TEACHER_SETTING_ACTIVITY_CLASS_NAME_STRING = "PARAM_JXHD_TEACHER_SETTING_ACTIVITY_CLASS_NAME_STRING";
    public static final String PARAM_JXHD_TEACHER_SETTING_ACTIVITY_UNIT_ID_LONG = "PARAM_JXHD_TEACHER_SETTING_ACTIVITY_UNIT_ID_LONG";


    //=================================== jxhd模块 end =======================================//

    //=================================== library模块 start =======================================//
    /** 忘记密码页面 cn.xxt.library.ui.gesture.GesturePwdForgetActivity */
    public static final String URL_LIBRARY_GESTURE_PWD_FORGET_ACTIVITY = "/library/GesturePwdForgetActivity";
    public static final String PARAM_LIBRARY_GESTURE_PWD_FORGET_ACTIVITY_TITLE_STRING = "PARAM_LIBRARY_GESTURE_PWD_FORGET_ACTIVITY_TITLE_STRING";
    public static final String PARAM_LIBRARY_GESTURE_PWD_FORGET_ACTIVITY_OPERATE_TYPE_INT = "PARAM_LIBRARY_GESTURE_PWD_FORGET_ACTIVITY_OPERATE_TYPE_INT";


    /** 手势密码主页面 cn.xxt.library.ui.gesture.GesturePwdMainActivity */
    public static final String URL_LIBRARY_GESTURE_PWD_MAIN_ACTIVITY = "/library/GesturePwdMainActivity";

    /** 设置密码页面 cn.xxt.library.ui.gesture.GesturePwdSettingFragment */
    public static final String URL_LIBRARY_GESTURE_PWD_SETTING_FRAGMENT = "/library/GesturePwdSettingFragment";

    /** 解锁页面 cn.xxt.library.ui.gesture.GesturePwdUnlockFragment */
    public static final String URL_LIBRARY_GESTURE_PWD_UNCLOCK_FRAGMENT = "/library/GesturePwdUnlockFragment";

    /** 选择联系人关系页面 cn.xxt.library.ui.relation.ContactRelationsActivity */
    public static final String URL_LIBRARY_CONTACT_RELATIONS_ACTIVITY = "/library/ContactRelationsActivity";
    public static final String PARAM_LIBRARY_CONTACT_RELATIONS_ACTIVITY_SELECTED_RELATION_ID_INT = "PARAM_LIBRARY_CONTACT_RELATIONS_ACTIVITY_SELECTED_RELATION_ID_INT";

    /** 短信服务开通页面 cn.xxt.library.ui.sms.SmsOpeningActivity */
    public static final String URL_LIBRARY_SMS_OPENING_ACTIVITY = "/library/SmsOpeningActivity";
    public static final String PARAM_LIBRARY_SMS_OPENING_ACTIVITY_HANDLE_FLAG_INT = "PARAM_LIBRARY_SMS_OPENING_ACTIVITY_HANDLE_FLAG_INT";
    public static final String PARAM_LIBRARY_SMS_OPENING_ACTIVITY_REQUEST_INTERFACE_OR_NOT_INT = "PARAM_LIBRARY_SMS_OPENING_ACTIVITY_REQUEST_INTERFACE_OR_NOT_INT";
    public static final String PARAM_LIBRARY_SMS_OPENING_ACTIVITY_FEATURE_ID_LONG = "PARAM_LIBRARY_SMS_OPENING_ACTIVITY_FEATURE_ID_LONG";
    public static final String PARAM_LIBRARY_SMS_OPENING_ACTIVITY_UNIT_ID_LONG = "PARAM_LIBRARY_SMS_OPENING_ACTIVITY_UNIT_ID_LONG";

    /** 短信服务的开通方式页面 cn.xxt.library.ui.sms.SmsOpeningModeActivity */
    public static final String URL_LIBRARY_SMS_OPENING_MODE_ACTIVITY = "/library/SmsOpeningModeActivity";
    public static final String PARAM_LIBRARY_SMS_OPENING_MODE_ACTIVITY_MOBILE_STRING = "PARAM_LIBRARY_SMS_OPENING_MODE_ACTIVITY_MOBILE_STRING";
    public static final String PARAM_LIBRARY_SMS_OPENING_MODE_ACTIVITY_OPEN_MODE_FLAG_INT = "PARAM_LIBRARY_SMS_OPENING_MODE_ACTIVITY_OPEN_MODE_FLAG_INT";
    public static final String PARAM_LIBRARY_SMS_OPENING_MODE_ACTIVITY_VERIFICATION_CODE_STRING = "PARAM_LIBRARY_SMS_OPENING_MODE_ACTIVITY_VERIFICATION_CODE_STRING";

    /** 设置学科信息页面 cn.xxt.library.ui.subjectinfo.SetSubjectInfoActivity */
    public static final String URL_LIBRARY_SET_SUBJECT_INFO_ACTIVITY = "/library/SetSubjectInfoActivity";

    /** 上传文件页面 cn.xxt.library.ui.fileuploadhelper.FileUploadActivity */
    public static final String URL_LIBRARY_FILE_UPLOAD_ACTIVITY = "/library/FileUploadActivity";
    public static final String PARAM_LIBRARY_FILE_UPLOAD_ACTIVITY_TITLE_STRING = "PARAM_LIBRARY_FILE_UPLOAD_ACTIVITY_TITLE_STRING";
    public static final String PARAM_LIBRARY_FILE_UPLOAD_ACTIVITY_ACTIVITY_ID_INT = "PARAM_LIBRARY_FILE_UPLOAD_ACTIVITY_ACTIVITY_ID_INT";
    public static final String PARAM_LIBRARY_FILE_UPLOAD_ACTIVITY_UPLOAD_FILE_URL_STRING = "PARAM_LIBRARY_FILE_UPLOAD_ACTIVITY_UPLOAD_FILE_URL_STRING";
    public static final String PARAM_LIBRARY_FILE_UPLOAD_ACTIVITY_RELEASE_CONTENT_URL_STRING = "PARAM_LIBRARY_FILE_UPLOAD_ACTIVITY_RELEASE_CONTENT_URL_STRING";
    public static final String PARAM_LIBRARY_FILE_UPLOAD_ACTIVITY_PARAM_TYPES_LIST = "PARAM_LIBRARY_FILE_UPLOAD_ACTIVITY_PARAM_TYPES_LIST";
    //=================================== library模块 end =======================================//



    //=================================== scanner模块 start =======================================//

    /** 扫一扫页面 cn.xxt.scanner.ui.notify.QRCodeScannerActivity */
    public static final String URL_SCANNER_QRCODE_SCANNER_ACTIVITY = "/scanner/QRCodeScannerActivity";
    public static final String PARAM_SCANNER_QRCODE_SCANNER_ACTIVITY_CALLBACK_FLAG = "PARAM_SCANNER_QRCODE_SCANNER_ACTIVITY_CALLBACK_FLAG";
    public static final String PARAM_SCANNER_QRCODE_SCANNER_ACTIVITY_USE_RXBUS_FLAG_BOOLEAN = "PARAM_SCANNER_QRCODE_SCANNER_ACTIVITY_USE_RXBUS_FLAG_BOOLEAN";

    //=================================== scanner模块 end =======================================//









    //=================================== school模块 start =======================================//

    /** 考勤根页面 cn.xxt.school.ui.clock.ClockActivity */
    public static final String URL_SCHOOL_CLOCK_ACTIVITY = "/school/ClockActivity";
    public static final String URL_SCHOOL_CLOCK_ACTIVITY_SELECT_TAB_TAG_STRING = "URL_SCHOOL_CLOCK_ACTIVITY_SELECT_TAB_TAG_STRING";



    /** 考勤Fragment cn.xxt.school.ui.clock.ClockFragment */
    public static final String URL_SCHOOL_CLOCK_FRAGMENT = "/school/ClockFragment";



    /** 考勤设置页面 cn.xxt.school.ui.clock.ClockSettingFragment */
    public static final String URL_SCHOOL_CLOCK_SETTING_FRAGMENT = "/school/ClockSettingFragment";



    /** 新建考勤组页面 cn.xxt.school.ui.clock.GroupAddActivity */
    public static final String URL_SCHOOL_GROUP_ADD_ACTIVITY = "/school/GroupAddActivity";



    /** 外勤打卡页面 cn.xxt.school.ui.clock.OutClockActivity */
    public static final String URL_SCHOOL_OUT_CLOCK_ACTIVITY = "/school/OutClockActivity";
    public static final String PARAM_SCHOOL_OUT_CLOCK_ACTIVITY_LAST_CLOCK_TIME_LONG = "PARAM_SCHOOL_OUT_CLOCK_ACTIVITY_LAST_CLOCK_TIME_LONG";
    public static final String PARAM_SCHOOL_OUT_CLOCK_ACTIVITY_CLOCK_GROUP_INFO_SERIALIZABLE = "PARAM_SCHOOL_OUT_CLOCK_ACTIVITY_CLOCK_GROUP_INFO_SERIALIZABLE";



    /** 校务主页面 cn.xxt.school.ui.homepage.SchoolHomepageActivity */
    public static final String URL_SCHOOL_SCHOOL_HOMEPAGE_ACTIVITY = "/school/SchoolHomepageActivity";



    /** 校务懒加载主页面 cn.xxt.school.ui.homepage.LazySchoolHomepageFragment */
    public static final String URL_SCHOOL_LAZY_SCHOOL_HOMEPAGE_FRAGMENT = "/school/LazySchoolHomepageFragment";



    /** 校务主页面 cn.xxt.school.ui.homepage.SchoolHomepageFragment */
    public static final String URL_SCHOOL_SCHOOL_HOMEPAGE_FRAGMENT = "/school/SchoolHomepageFragment";



    /** 会议列表页面 cn.xxt.school.ui.meeting.MeetingListActivity */
    public static final String URL_SCHOOL_MEETING_LIST_ACTIVITY = "/school/MeetingListActivity";



    /** 新建会议页面 cn.xxt.school.ui.meeting.MeetingPublishActivity */
    public static final String URL_SCHOOL_MEETING_PUBLISH_ACTIVITY = "/school/MeetingPublishActivity";
    public static final String PARAM_SCHOOL_MEETING_PUBLISH_ACTIVITY_GOTO_LIST_AFTER_PUBLISH_FLAG_BOOLEAN = "PARAM_SCHOOL_MEETING_PUBLISH_ACTIVITY_GOTO_LIST_AFTER_PUBLISH_FLAG_BOOLEAN";



    /** 定时会议列表页面 cn.xxt.school.ui.meeting.TimingMeetingListActivity */
    public static final String URL_SCHOOL_TIMING_MEETING_LIST_ACTIVITY = "/school/TimingMeetingListActivity";



    /** 所有公文页面 cn.xxt.school.ui.offdoc.AllOffDocFragment */
    public static final String URL_SCHOOL_ALL_OFF_DOC_FRAGMENT = "/school/AllOffDocFragment";



    /** 我发的公文页面 cn.xxt.school.ui.offdoc.MyOffDocFragment */
    public static final String URL_SCHOOL_MY_OFF_DOC_FRAGMENT = "/school/MyOffDocFragment";



    /** 新建公文页面 cn.xxt.school.ui.offdoc.OffDocPublishActivity */
    public static final String URL_SCHOOL_OFF_DOC_PUBLISH_ACTIVITYY = "/school/OffDocPublishActivity";
    public static final String PARAM_SCHOOL_OFF_DOC_PUBLISH_ACTIVITY_GOTO_LIST_AFTER_PUBLISH_FLAG_BOOLEAN = "PARAM_SCHOOL_OFF_DOC_PUBLISH_ACTIVITY_GOTO_LIST_AFTER_PUBLISH_FLAG_BOOLEAN";



    /** 公文首页 cn.xxt.school.ui.offdoc.OffDocActivity */
    public static final String URL_SCHOOL_OFF_DOC_ACTIVITY = "/school/OffDocActivity";



    /** 定时公文页面 cn.xxt.school.ui.offdoc.TimingOffDocSchoolActivity */
    public static final String URL_SCHOOL_TIMING_OFF_DOC_SCHOOL_ACTIVITY = "/school/TimingOffDocSchoolActivity";

    //=================================== school模块 end =======================================//











    //=================================== setting模块 start =======================================//

    /** 关于校讯通页面 cn.xxt.setting.ui.about.AboutActivity */
    public static final String URL_SETTING_ABOUT_ACTIVITY = "/setting/AboutActivity";



    /** 添加孩子页面 cn.xxt.setting.ui.children.ChildAddActivity */
    public static final String URL_SETTING_CHILD_ADD_ACTIVITY = "/setting/ChildAddActivity";



    /** 添加班级页面 cn.xxt.setting.ui.children.ClassAddActivity */
    public static final String URL_SETTING_CLASS_ADD_ACTIVITY = "/setting/ClassAddActivity";
    public final static String PARAM_SETTING_CLASS_ADD_ACTIVITY_STU_ID_LONG = "PARAM_SETTING_CLASS_ADD_ACTIVITY_STU_ID_LONG";



    /** 添加联系人页面 cn.xxt.setting.ui.children.ContactAddActivity */
    public static final String URL_SETTING_CONTACT_ADD_ACTIVITY = "/setting/ContactAddActivity";
    public final static String PARAM_SETTING_CONTACT_ADD_ACTIVITY_STU_ID_LONG = "PARAM_SETTING_CONTACT_ADD_ACTIVITY_STU_ID_LONG";
    public final static String PARAM_SETTING_CONTACT_ADD_ACTIVITY_UNIT_ID_LONG = "PARAM_SETTING_CONTACT_ADD_ACTIVITY_UNIT_ID_LONG";



    /** 修改联系人页面 cn.xxt.setting.ui.children.ContactModifyActivity */
    public static final String URL_SETTING_CONTACT_MODIFY_ACTIVITY = "/setting/ContactModifyActivity";
    public final static String PARAM_SETTING_CONTACT_MODIFY_ACTIVITY_UNIT_ID_LONG = "PARAM_SETTING_CONTACT_MODIFY_ACTIVITY_UNIT_ID_LONG";
    public final static String PARAM_SETTING_CONTACT_MODIFY_ACTIVITY_FEATURE_ID_LONG = "PARAM_SETTING_CONTACT_MODIFY_ACTIVITY_FEATURE_ID_LONG";
    public final static String PARAM_SETTING_CONTACT_MODIFY_ACTIVITY_FEATURE_NAME_STRING = "PARAM_SETTING_CONTACT_MODIFY_ACTIVITY_FEATURE_NAME_STRING";
    public final static String PARAM_SETTING_CONTACT_MODIFY_ACTIVITY_DESCRIPTION_STRING = "PARAM_SETTING_CONTACT_MODIFY_ACTIVITY_DESCRIPTION_STRING";
    public final static String PARAM_SETTING_CONTACT_MODIFY_ACTIVITY_RELATION_ID_INT = "PARAM_SETTING_CONTACT_MODIFY_ACTIVITY_RELATION_ID_INT";
    public final static String PARAM_SETTING_CONTACT_MODIFY_ACTIVITY_MOBILE_STRING = "PARAM_SETTING_CONTACT_MODIFY_ACTIVITY_MOBILE_STRING";



    /** 我的孩子页面 cn.xxt.setting.ui.children.MyChildrenActivity */
    public static final String URL_SETTING_MY_CHILDREN_ACTIVITY = "/setting/MyChildrenActivity";



    /** '我'页面 cn.xxt.setting.ui.my.MyActivity */
    public static final String URL_SETTING_MY_ACTIVITY = "/setting/MyActivity";



    /** '我'懒加载页面 cn.xxt.setting.ui.my.LazyMyFragment */
    public static final String URL_SETTING_LAZY_MY_FRAGMENT = "/setting/LazyMyFragment";



    /** '我'页面Fragment cn.xxt.setting.ui.my.MyFragment */
    public static final String URL_SETTING_MY_FRAGMENT = "/setting/MyFragment";



    /** 个人信息页面 cn.xxt.setting.ui.personalinfo.PersonalInfoActivity */
    public static final String URL_SETTING_PERSONAL_INFO_ACTIVITY = "/setting/PersonalInfoActivity";



    /** 设置推送消息通知页面 cn.xxt.setting.ui.push.PushMsgSettingActivity */
    public static final String URL_SETTING_PUSH_MSG_SETTING_ACTIVITY = "/setting/PushMsgSettingActivity";



    /** 账号与安全页面 cn.xxt.setting.ui.setting.AccountAndSafeActivity */
    public static final String URL_SETTING_ACCOUNT_AND_SAFE_ACTIVITY = "/setting/AccountAndSafeActivity";



    /** 缓存清理页面 cn.xxt.setting.ui.setting.ClearCacheActivity */
    public static final String URL_SETTING_CLEAR_CACHE_ACTIVITY = "/setting/ClearCacheActivity";



    /** 内部调试页面 cn.xxt.setting.ui.setting.debug.DebugActivity */
    public static final String URL_SETTING_DEBUG_ACTIVITY = "/setting/DebugActivity";



    /** hosts配置页面 cn.xxt.setting.ui.setting.debug.HostsActivity */
    public static final String URL_SETTING_HOSTS_ACTIVITY = "/setting/HostsActivity";


    /** 单个服务器hosts配置页面 cn.xxt.setting.ui.setting.debug.HostsSetActivity */
    public static final String URL_SETTING_HOSTS_SET_ACTIVITY = "/setting/HostsSetActivity";
    public static final String PARAM_SETTING_HOSTS_SET_ACTIVITY_IP_STRING = "PARAM_SETTING_HOSTS_SET_ACTIVITY_IP_STRING";



    /** 修改密码页面 cn.xxt.setting.ui.setting.PasswordModifyActivity */
    public static final String URL_SETTING_PASSWORD_MODIFY_ACTIVITY = "/setting/PasswordModifyActivity";

    /** 设置、修改密码主activity cn.xxt.setting.ui.setting.passwordmanager.PasswordManagerActivity */
    public static final String URL_SETTING_PASSWORD_MANAGER_ACTIVITY = "/setting/PasswordManagerActivity";
    public static final String PARAM_SETTING_PASSWORD_MANAGER_ACTIVITY_IS_SET_PWD_BOOLEAN = "PARAM_SETTING_PASSWORD_MANAGER_ACTIVITY_IS_SET_PWD_BOOLEAN";
    public static final String PARAM_SETTING_PASSWORD_MANAGER_ACTIVITY_IS_FROM_LOGIN_BOOLEAN = "PARAM_SETTING_PASSWORD_MANAGER_ACTIVITY_IS_FROM_LOGIN_BOOLEAN";
    public static final String PARAM_SETTING_PASSWORD_MANAGER_ACTIVITY_NOTIFY_TOKEN_STRING = "PARAM_SETTING_PASSWORD_MANAGER_ACTIVITY_NOTIFY_TOKEN_STRING";
    public static final String PARAM_SETTING_PASSWORD_MANAGER_ACTIVITY_IS_FROM_INPUT_CODE_BOOLEAN = "PARAM_SETTING_PASSWORD_MANAGER_ACTIVITY_IS_FROM_INPUT_CODE_BOOLEAN";


    /** 设置密码Fragment cn.xxt.setting.ui.setting.passwordmanager.SetPasswordFragment */
    public static final String URL_SETTING_SET_PASSWORD_FRAGMENT = "/setting/SetPasswordFragment";
    public static final String PARAM_SETTING_SET_PASSWORD_FRAGMENT_IS_FROM_LOGIN_BOOLEAN = "PARAM_SETTING_SET_PASSWORD_FRAGMENT_IS_FROM_LOGIN_BOOLEAN";
    public static final String PARAM_SETTING_SET_PASSWORD_FRAGMENT_NOTIFY_TOKEN_STRING = "PARAM_SETTING_SET_PASSWORD_FRAGMENT_NOTIFY_TOKEN_STRING";
    public static final String PARAM_SETTING_SET_PASSWORD_FRAGMENT_IS_FROM_INPUT_CODE_BOOLEAN = "PARAM_SETTING_SET_PASSWORD_FRAGMENT_IS_FROM_INPUT_CODE_BOOLEAN";

    /** 修改密码Fragment cn.xxt.setting.ui.setting.passwordmanager.ChangePasswordFragment */
    public static final String URL_SETTING_CHANGE_PASSWORD_FRAGMENT = "/setting/ChangePasswordFragment";

    /** 输入验证码页面 cn.xxt.setting.ui.setting.passwordmanager.InputCodeActivity */
    public static final String URL_SETTING_INPUT_CODE_ACTIVITY = "/setting/InputCodeActivity";

    /** 设置页面 cn.xxt.setting.ui.setting.SettingActivity */
    public static final String URL_SETTING_SETTING_ACTIVITY = "/setting/SettingActivity";

    /** 选择套餐页面 cn.xxt.setting.ui.monthly.MonthlyProductSelectTariffActivity */
    public static final String URL_SETTING_MONTHLY_PRODUCT_SELECT_TARIFF_ACTIVITY = "/setting/MonthlyProductSelectTariffActivity";
    public static final String PARAM_SETTING_MONTHLY_PRODUCT_SELECT_TARIFF_ACTIVITY_MONTHLY_PRODUCT = "PARAM_SETTING_MONTHLY_PRODUCT_SELECT_TARIFF_ACTIVITY_MONTHLY_PRODUCT";

    /** 选择支付方式支付页面 cn.xxt.setting.ui.monthly.MonthlyProductPayDialogFragment */
    public static final String URL_SETTING_MONTHLY_PRODUCT_PAY_DIALOG_FRAGMENT = "/setting/MonthlyProductPayDialogFragment";
    public static final String PARAM_SETTING_MONTHLY_PRODUCT_PAY_DIALOG_FRAGMENT_MONTHLY_PRODUCT = "PARAM_SETTING_MONTHLY_PRODUCT_PAY_DIALOG_FRAGMENT_MONTHLY_PRODUCT";
    public static final String PARAM_SETTING_MONTHLY_PRODUCT_PAY_DIALOG_FRAGMENT_TARIFF_MODEL = "PARAM_SETTING_MONTHLY_PRODUCT_PAY_DIALOG_FRAGMENT_TARIFF_MODEL";

    /** 选择套餐页面 cn.xxt.setting.ui.monthly.MonthlyProductOrderRecordActivity */
    public static final String URL_SETTING_MONTHLY_PRODUCT_ORDER_RECORD_ACTIVITY = "/setting/MonthlyProductOrderRecordActivity";

    /** 包月产品列表页面 cn.xxt.setting.ui.monthly.MonthlyProductListActivity */
    public static final String URL_SETTING_MONTHLY_PRODUCT_LIST_ACTIVITY = "/setting/MonthlyProductListActivity";

    /** 支付结果页面 cn.xxt.setting.ui.monthly.MonthlyProductPayResultActivity */
    public static final String URL_SETTING_MONTHLY_PRODUCT_PAY_RESULT_ACTIVITY = "/setting/MonthlyProductPayResultActivity";

    public static final String PARAM_SETTING_MONTHLY_PRODUCT_PAY_RESULT_ACTIVITY_ORDER_DESC_STRING = "PARAM_SETTING_MONTHLY_PRODUCT_PAY_RESULT_ACTIVITY_ORDER_DESC_STRING";

    public static final String PARAM_SETTING_MONTHLY_PRODUCT_PAY_RESULT_ACTIVITY_CHARGE_BOOLEAN = "PARAM_SETTING_PASSWORD_MANAGER_ACTIVITY_CHARGE_BOOLEAN";

    public static final String PARAM_SETTING_MONTHLY_PRODUCT_PAY_RESULT_ACTIVITY_VALIDITY_PERIOD_STRING = "PARAM_SETTING_MONTHLY_PRODUCT_PAY_RESULT_ACTIVITY_VALIDITY_PERIOD_STRING";

    public static final String PARAM_SETTING_MONTHLY_PRODUCT_PAY_RESULT_ACTIVITY_HINT_STRING = "PARAM_SETTING_MONTHLY_PRODUCT_PAY_RESULT_ACTIVITY_HINT_STRING";

    //=================================== setting模块 end =======================================//








    //=================================== webview模块 start =======================================//
    /** WebViewPageActivity cn.xxt.webview.ui.webviewpage.WebViewPageActivity */
    public final static String URL_WEBVIEW_WEB_VIEW_PAGE_ACTIVITY = "/webview/WebViewPageActivity";
    public final static String PARAM_WEBVIEW_WEB_VIEW_PAGE_ACTIVITY_PATH_STR_STRING = "PARAM_WEBVIEW_WEB_VIEW_PAGE_ACTIVITY_PATH_STR_STRING";

    /** WebViewPageFragment cn.xxt.webview.ui.webviewpage.LazyWebViewPageFragment */
    public final static String URL_WEBVIEW_LAZY_WEB_VIEW_PAGE_FRAGMENT = "/webview/LazyWebViewPageFragment";

    /** WebViewPageFragment cn.xxt.webview.ui.webviewpage.WebViewPageFragment */
    public final static String URL_WEBVIEW_WEB_VIEW_PAGE_FRAGMENT = "/webview/WebViewPageFragment";
    public final static String PARAM_WEBVIEW_WEB_VIEW_PAGE_FRAGMENT_URL_STR_STRING = "PARAM_WEBVIEW_WEB_VIEW_PAGE_FRAGMENT_URL_STR_STRING";


    public final static String URL_WEBVIEW_SIMPLE_WEB_VIEW_ACTIVITY = "/webview/SimpleWebViewActivity";
    public final static String PARAM_WEBVIEW_SIMPLE_WEB_VIEW_ACTIVITY_PATH_STR_STRING = "PARAM_WEBVIEW_SIMPLE_WEB_VIEW_ACTIVITY_PATH_STR_STRING";
    public final static String PARAM_WEBVIEW_SIMPLE_WEB_VIEW_ACTIVITY_IS_SHOW_PRIVACY_BOOLEAN = "PARAM_WEBVIEW_SIMPLE_WEB_VIEW_ACTIVITY_IS_SHOW_PRIVACY_BOOLEAN";
    public final static String PARAM_WEBVIEW_SIMPLE_WEB_VIEW_ACTIVITY_CHECK_TIP_STRING = "PARAM_WEBVIEW_SIMPLE_WEB_VIEW_ACTIVITY_CHECK_TIP_STRING";

    public final static String URL_WEBVIEW_SIMPLE_WEB_VIEW_FRAGMENT = "/webview/SimpleWebViewFragment";
    public final static String PARAM_WEBVIEW_SIMPLE_WEB_VIEW_FRAGMENT_PATH_STR_STRING = "PARAM_WEBVIEW_SIMPLE_WEB_VIEW_FRAGMENT_PATH_STR_STRING";

    /** 视频播放页面 cn.xxt.webview.ui.videoplay.VideoActivity */
    public final static String URL_WEBVIEW_VIDEO_ACTIVITY = "/webview/VideoActivity";
    public final static String PARAM_WEBVIEW_VIDEO_ACTIVITY_URL_STR_STRING = "PARAM_WEBVIEW_VIDEO_ACTIVITY_URL_STR_STRING";

    /** 视频播放页面 cn.xxt.webview.ui.videoplay.ScreenProjectionActivity */
    public final static String URL_WEBVIEW_SCREEN_PROJECTION_ACTIVITY = "/webview/ScreenProjectionActivity";
    public final static String PARAM_WEBVIEW_SCREEN_PROJECTION_ACTIVITY_VIDEO_URL_STRING = "PARAM_WEBVIEW_SCREEN_PROJECTION_ACTIVITY_VIDEO_URL_STRING";
    public final static String PARAM_WEBVIEW_SCREEN_PROJECTION_ACTIVITY_START_POSITION_INT = "PARAM_WEBVIEW_SCREEN_PROJECTION_ACTIVITY_START_POSITION_INT";


    /** 文件预览页面 cn.xxt.webview.ui.fileOpen.DocPreviewActivity*/
    public final static String URL_WEBVIEW_DOC_PREVIEW_ACTIVITY = "/webview/DocPreviewActivity";
    public final static String PARAM_WEBVIEW_DOC_PREVIEW_ACTIVITY_FILE_NAME_STRING = "PARAM_WEBVIEW_DOC_PREVIEW_ACTIVITY_FILE_NAME_STRING";
    public final static String PARAM_WEBVIEW_DOC_PREVIEW_ACTIVITY_FILE_PATH_STRING = "PARAM_WEBVIEW_DOC_PREVIEW_ACTIVITY_FILE_PATH_STRING";



    /** 文件预览Fragment cn.xxt.webview.ui.fileOpen.DocPreviewActivity*/
    public final static String URL_WEBVIEW_DOC_PREVIEW_FRAGMENT = "/webview/DocPreviewFragment";
    public final static String PARAM_WEBVIEW_DOC_PREVIEW_FRAGMENT_FILE_PATH_STRING = "PARAM_WEBVIEW_DOC_PREVIEW_FRAGMENT_FILE_PATH_STRING";
    public final static String PARAM_WEBVIEW_DOC_PREVIEW_FRAGMENT_FILE_NAME_STRING = "PARAM_WEBVIEW_DOC_PREVIEW_FRAGMENT_FILE_NAME_STRING";


    /** 内部调试页面 cn.xxt.webview.ui.WebViewTestActivity */
    public static final String URL_WEBVIEW_WEB_VIEW_TEST_ACTIVITY = "/webview/WebViewTestActivity";

    //=================================== webview模块 end =======================================//








    //=================================== file模块 start =======================================//
    /** 文件选择器主页面activity cn.xxt.file.ui.selector.FileSelectorMainActivity*/
    public final static String URL_FILE_FILE_SELECTOR_MAIN_ACTIVITY = "/xxtFile/FileSelectorMainActivity";
    public final static String PARAM_FILE_FILE_SELECTOR_MAIN_ACTIVITY_WEBID = "PARAM_FILE_FILE_SELECTOR_MAIN_ACTIVITY_WEBID";
    public final static String PARAM_FILE_FILE_SELECTOR_MAIN_ACTIVITY_FILE_SELECT_NUM_MAXIMUM = "PARAM_FILE_FILE_SELECTOR_MAIN_ACTIVITY_FILE_SELECT_NUM_MAXIMUM";
    public final static String PARAM_FILE_FILE_SELECTOR_MAIN_ACTIVITY_FILE_SELECT_SIZE_MAXIMUM = "PARAM_FILE_FILE_SELECTOR_MAIN_ACTIVITY_FILE_SELECT_SIZE_MAXIMUM";
    public final static String PARAM_FILE_FILE_SELECTOR_MAIN_ACTIVITY_SHOW_LOCAL_FLAG = "PARAM_FILE_FILE_SELECTOR_MAIN_ACTIVITY_SHOW_LOCAL_FLAG";
    public final static String PARAM_FILE_FILE_SELECTOR_MAIN_ACTIVITY_SHOW_RECENT_FLAG = "PARAM_FILE_FILE_SELECTOR_MAIN_ACTIVITY_SHOW_RECENT_FLAG";
    public final static String PARAM_FILE_FILE_SELECTOR_MAIN_ACTIVITY_FILE_TYPE_LIST = "PARAM_FILE_FILE_SELECTOR_MAIN_ACTIVITY_FILE_TYPE_LIST";
    public final static String PARAM_FILE_FILE_SELECTOR_MAIN_ACTIVITY_HAS_SELECTED_FILE_LIST = "PARAM_FILE_FILE_SELECTOR_MAIN_ACTIVITY_HAS_SELECTED_FILE_LIST";



    /** 文件管理主页面activity cn.xxt.file.ui.manager.FileManagerMainActivity*/
    public final static String URL_FILE_FILE_MANAGER_MAIN_ACTIVITY = "/xxtFile/FileManagerMainActivity";
    public final static String PARAM_FILE_FILE_MANAGER_MAIN_ACTIVITY_WEBID = "PARAM_FILE_FILE_MANAGER_MAIN_ACTIVITY_WEBID";
    public final static String PARAM_FILE_FILE_MANAGER_MAIN_ACTIVITY_FILE_SELECT_NUM_MAXIMUM = "PARAM_FILE_FILE_MANAGER_MAIN_ACTIVITY_FILE_SELECT_NUM_MAXIMUM";
    public final static String PARAM_FILE_FILE_MANAGER_MAIN_ACTIVITY_FILE_BATCH_DOWNLOADN_MAXIMUM = "PARAM_FILE_FILE_MANAGER_MAIN_ACTIVITY_FILE_BATCH_DOWNLOADN_MAXIMUM";
    public final static String PARAM_FILE_FILE_MANAGER_MAIN_ACTIVITY_FILE_TYPE_LIST = "PARAM_FILE_FILE_MANAGER_MAIN_ACTIVITY_FILE_TYPE_LIST";



    /** 文件管理器-本机文件主页面activity cn.xxt.file.ui.manager.FileLocalManagerMainActivity*/
    public final static String URL_FILE_FILE_LOCAL_MANAGER_MAIN_ACTIVITY = "/xxtFile/FileLocalManagerMainActivity";
    public final static String PARAM_FILE_FILE_LOCAL_MANAGER_MAIN_ACTIVITY_WEBID = "PARAM_FILE_FILE_LOCAL_MANAGER_MAIN_ACTIVITY_WEBID";
    public final static String PARAM_FILE_FILE_LOCAL_MANAGER_MAIN_ACTIVITY_FILE_TYPE_LIST = "PARAM_FILE_FILE_LOCAL_MANAGER_MAIN_ACTIVITY_FILE_TYPE_LIST";



    /** 文件管理器-最近文件主页面activity cn.xxt.file.ui.manager.FileRecentManagerMainActivity*/
    public final static String URL_FILE_FILE_RECENT_MANAGER_MAIN_ACTIVITY = "/xxtFile/FileRecentManagerMainActivity";
    public final static String PARAM_FILE_FILE_RECENT_MANAGER_MAIN_ACTIVITY_WEBID = "PARAM_FILE_FILE_RECENT_MANAGER_MAIN_ACTIVITY_WEBID";
    public final static String PARAM_FILE_FILE_RECENT_MANAGER_MAIN_ACTIVITY_BATCH_DOWNLOAD_MAXIMUM = "PARAM_FILE_FILE_RECENT_MANAGER_MAIN_ACTIVITY_BATCH_DOWNLOAD_MAXIMUM";
    public final static String PARAM_FILE_FILE_RECENT_MANAGER_MAIN_ACTIVITY_FILE_TYPE_LIST = "PARAM_FILE_FILE_RECENT_MANAGER_MAIN_ACTIVITY_FILE_TYPE_LIST";



    /** 本机文件主页面fragment cn.xxt.file.ui.fileFragment.FileLocalMainFragment*/
    public final static String URL_FILE_FILE_LOCAL_MAIN_FRAGMENT = "/xxtFile/FileLocalMainFragment";
    public final static String PARAM_FILE_FILE_LOCAL_MAIN_FRAGMENT_WEBID = "PARAM_FILE_FILE_LOCAL_MAIN_FRAGMENT_WEBID";
    public final static String PARAM_FILE_FILE_LOCAL_MAIN_FRAGMENT_FILE_TYPE_LIST = "PARAM_FILE_FILE_LOCAL_MAIN_FRAGMENT_FILE_TYPE_LIST";



    /** 最近文件主页面fragment cn.xxt.file.ui.fileFragment.FileRecentMainFragment*/
    public final static String URL_FILE_FILE_RECENT_MAIN_FRAGMENT = "/xxtFile/FileRecentMainFragment";
    public final static String PARAM_FILE_FILE_RECENT_MAIN_FRAGMENT_WEBID = "PARAM_FILE_FILE_RECENT_MAIN_FRAGMENT_WEBID";
    public final static String PARAM_FILE_FILE_RECENT_MAIN_FRAGMENT_BATCH_DOWNLOAD_MAXIMUM = "PARAM_FILE_FILE_RECENT_MAIN_FRAGMENT_BATCH_DOWNLOAD_MAXIMUM";
    public final static String PARAM_FILE_FILE_RECENT_MAIN_FRAGMENT_FILE_TYPE_LIST = "PARAM_FILE_FILE_RECENT_MAIN_FRAGMENT_FILE_TYPE_LIST";



    /** 全部文件页面fragment cn.xxt.file.ui.fileFragment.AllFragment*/
    public final static String URL_FILE_ALL_FRAGMENT = "/xxtFile/AllFragment";



    /** 音乐文件页面fragment cn.xxt.file.ui.fileFragment.AudioFragment*/
    public final static String URL_FILE_AUDIO_FRAGMENT = "/xxtFile/AudioFragment";
    public final static String PARAM_FILE_AUDIO_FRAGMENT_DATA_SOURCE = "PARAM_FILE_AUDIO_FRAGMENT_DATA_SOURCE";



    /** 文档文件页面fragment cn.xxt.file.ui.fileFragment.DocFragment*/
    public final static String URL_FILE_DOC_FRAGMENT = "/xxtFile/DocFragment";
    public final static String PARAM_FILE_DOC_FRAGMENT_DATA_SOURCE = "PARAM_FILE_DOC_FRAGMENT_DATA_SOURCE";


    /** 图片文件页面fragment cn.xxt.file.ui.fileFragment.PhotoFragment*/
    public final static String URL_FILE_PHOTO_FRAGMENT = "/xxtFile/PhotoFragment";
    public final static String PARAM_FILE_PHOTO_FRAGMENT_DATA_SOURCE = "PARAM_FILE_PHOTO_FRAGMENT_DATA_SOURCE";


    /** 其他文件页面fragment cn.xxt.file.ui.fileFragment.OtherFragment*/
    public final static String URL_FILE_OTHER_FRAGMENT = "/xxtFile/OtherFragment";
    public final static String PARAM_FILE_OTHER_FRAGMENT_DATA_SOURCE = "PARAM_FILE_OTHER_FRAGMENT_DATA_SOURCE";


    /** 打开文件主页面activity cn.xxt.file.ui.manager.FileOpenActivity*/
    public final static String URL_FILE_FILE_OPEN_ACTIVITY = "/xxtFile/FileOpenActivity";
    public final static String PARAM_FILE_FILE_OPEN_ACTIVITY_FILE_INFO = "PARAM_FILE_FILE_OPEN_ACTIVITY_FILE_INFO";



    /** apk安装fragment cn.xxt.file.ui.manager.FileOpenApkInstallFragment*/
    public final static String URL_FILE_FILE_OPEN_APK_INSTALL_FRAGMENT = "/xxtFile/FileOpenApkInstallFragment";
    public final static String PARAM_FILE_FILE_OPEN_APK_INSTALL_FRAGMENT_FILE_INFO = "PARAM_FILE_FILE_OPEN_APK_INSTALL_FRAGMENT_FILE_INFO";


    /**音乐播放fragment cn.xxt.file.ui.manager.FileOpenAudioPlayFragment*/
    public final static String URL_FILE_FILE_OPEN_AUDIO_PLAY_FRAGMENT = "/xxtFile/FileOpenAudioPlayFragment";
    public final static String PARAM_FILE_FILE_OPEN_AUDIO_PLAY_FRAGMENT_FILE_INFO = "PARAM_FILE_FILE_OPEN_AUDIO_PLAY_FRAGMENT_FILE_INFO";



    /** 文件下载fragment cn.xxt.file.ui.manager.FileOpenDownloadFragment*/
    public final static String URL_FILE_FILE_OPEN_DOWNLOAD_FRAGMENT = "/xxtFile/FileOpenDownloadFragment";
    public final static String PARAM_FILE_FILE_OPEN_DOWNLOAD_FRAGMENT_FILE_INFO = "PARAM_FILE_FILE_OPEN_DOWNLOAD_FRAGMENT_FILE_INFO";



    /** 文件失效fragment cn.xxt.file.ui.manager.FileOpenUnVailableFragment*/
    public final static String URL_FILE_FILE_OPEN_UNVAILABLE_FRAGMENT = "/xxtFile/FileOpenUnVailableFragment";
    public final static String PARAM_FILE_FILE_OPEN_UNVAILABLE_FRAGMENT_FILE_INFO = "PARAM_FILE_FILE_OPEN_UNVAILABLE_FRAGMENT_FILE_INFO";

    //=================================== file模块 end =======================================//


    //=================================== 三方业务模块 start =======================================//
    /** 文件选择器主页面activity cn.xxt.cooperationbusiness.ui.mcscloud.McsMainActivity*/
    public final static String URL_COOPER_MCS_MAIN_ACTIVITY = "/cooper/McsMainActivity";

    /** 云视讯会议主页面activity cn.xxt.cooperationbusiness.ui.ysx.YsxMeetingHomeActivity  */
    public final static String URL_COOPER_YSX_MEETING_HOME_ACTIVITY = "/cooper/YsxMeetingHomeActivity";
    public final static String PARAM_COOPER_YSX_MEETING_HOME_ACTIVITY_MEETING_ID_INT = "PARAM_COOPER_YSX_MEETING_HOME_ACTIVITY_MEETING_ID_INT";

    /** 预约会议列表fragment cn.xxt.cooperationbusiness.ui.ysx.YsxAppointmentMeetingListFragment */
    public final static String URL_COOPER_YSX_APPOINTMENT_MEETING_LIST_FRAGMENT = "/cooper/YsxAppointmentMeetingListFragment";
    public final static String PARAM_COOPER_YSX_APPOINTMENT_MEETING_LIST_FRAGMENT_MEETING_ID_INT = "PARAM_COOPER_YSX_APPOINTMENT_MEETING_LIST_FRAGMENT_MEETING_ID_INT";

    /** 历史会议列表fragment cn.xxt.cooperationbusiness.ui.ysx.YsxHistoricalMeetingListFragment */
    public final static String URL_COOPER_YSX_HISTORICAL_MEETING_LIST_FRAGMENT = "/cooper/YsxHistoricalMeetingListFragment";

    /** 创建会议页面 cn.xxt.cooperationbusiness.ui.ysx.YsxCreateMeetingActivity */
    public static final String URL_COOPER_YSX_CREATE_MEETING_ACTIVITY = "/cooper/YsxCreateMeetingActivity";
    public final static String PARAM_URL_COOPER_YSX_CREATE_MEETING_ACTIVITY_MEETING_TYPE = "PARAM_URL_COOPER_YSX_CREATE_MEETING_ACTIVITY_MEETING_TYPE";

    /** 创建会议页面 cn.xxt.cooperationbusiness.ui.ysx.YsxMeetingActivity */
    public static final String URL_COOPER_YSX_MEETING_ACTIVITY = "/cooper/YsxMeetingActivity";
    public final static String PARAM_URL_COOPER_YSX_MEETING_ACTIVITY_YSXMODEL = "PARAM_URL_COOPER_YSX_MEETING_ACTIVITY_YSXMODEL";

    /** 云视讯会议详情页面activity cn.xxt.cooperationbusiness.ui.ysx.YsxMeetingDetailActivity  */
    public final static String URL_COOPER_YSX_MEETING_DETAIL_ACTIVITY = "/cooper/YsxMeetingDetailActivity";
    public final static String PARAM_COOPER_YSX_MEETING_DETAIL_ACTIVITY_MEETING_ID_INT = "PARAM_COOPER_YSX_MEETING_DETAIL_ACTIVITY_MEETING_ID_INT";

    /** 加入云视讯会议页面activity cn.xxt.cooperationbusiness.ui.ysx.YsxJoinMeetingActivity  */
    public final static String URL_COOPER_YSX_JOIN_MEETING_ACTIVITY = "/cooper/YsxJoinMeetingActivity";

    //=================================== 三方业务模块 end =======================================//


    //=================================== jxt模块 start =======================================//

    /** 选择联系人页面 cn.xxt.jxt.ui.contactchooser.ContactsChooserActivity */
    public final static String URL_JXT_CONTACTS_CHOOSER_ACTIVITY = "/jxt/ContactsChooserActivity";
    public final static String PARAM_JXT_CONTACTS_CHOOSER_ACTIVITY_FORWARD_CONTENT_STRING = "PARAM_CONTACTS_CHOOSER_ACTIVITY_FORWARD_CONTENT_STRING";
    public final static String PARAM_JXT_CONTACTS_CHOOSER_ACTIVITY_CHOOSED_LIST_SERIALIZABLE = "PARAM_JXT_CONTACTS_CHOOSER_ACTIVITY_CHOOSED_LIST_SERIALIZABLE";
    public final static String PARAM_JXT_CONTACTS_CHOOSER_ACTIVITY_CONTACT_CHOOSE_WHETER_RXBUS = "PARAM_JXT_CONTACTS_CHOOSER_ACTIVITY_CONTACT_CHOOSE_WHETER_RXBUS";
    public final static String PARAM_JXT_CONTACTS_CHOOSER_ACTIVITY_SHOW_PERSON_TYPE_FLAG_INT = "PARAM_JXT_CONTACTS_CHOOSER_ACTIVITY_SHOW_PERSON_TYPE_FLAG_INT";
    public final static String PARAM_JXT_CONTACTS_CHOOSER_ACTIVITY_SELECT_SINGLE_PERSON_TYPE_FLAG_BOOLEAN = "PARAM_JXT_CONTACTS_CHOOSER_ACTIVITY_SELECT_SINGLE_PERSON_TYPE_FLAG_BOOLEAN";

    /** 会话详情页面 cn.xxt.jxt.ui.jxlx.GroupDetailJxtActivity */
    public final static String URL_JXT_GROUP_DETAIL_JXT_ACTIVITY = "/jxt/GroupDetailJxtActivity";

    /** 家校联系首页页面 cn.xxt.jxt.ui.jxlx.JxlxActivity */
    public final static String URL_JXT_JXLX_ACTIVITY = "/jxt/JxlxActivity";

    /** 家校通提醒页面 cn.xxt.jxt.ui.jxlx.JxtTipFragment */
    public final static String URL_JXT_JXT_TIP_FRAGMENT = "/jxt/JxtTipFragment";

    /** 懒加载会话列表页面 cn.xxt.jxt.ui.jxlx.LazyJxtMsgListFragment */
    public static final String URL_JXT_JXT_LAZY_MSG_LIST_FRAGMENT = "/jxt/LazyJxtMsgListFragment";

    /** 会话列表页面 cn.xxt.jxt.ui.jxlx.MsgListFragment */
    public static final String URL_JXT_MSG_LIST_FRAGMENT = "/jxt/MsgListFragment";

    /** 收发信息页面 cn.xxt.jxt.ui.jxlx.MsgReceiveAndSendJxtActivity */
    public final static String URL_JXT_MSG_RECEIVE_SEND_JXT_ACTIVITY = "/jxt/MsgReceiveAndSendJxtActivity";

    //=================================== jxt模块 end =======================================//


    //===================================== ocr模块 start =======================================//
    /** 文本识别页面 com.baidu.paddle.lite.demo.ocr.OCRActivity */
    public final static String URL_OCR_OCR_ACTIVITY = "/ocr/OCRActivity";
    public final static String PARAM_OCR_OCR_ACTIVITY_SHOW_CHOOSE_IMAGE_WHEN_ENTER_FLAG_BOOLEAN = "PARAM_OCR_OCR_ACTIVITY_SHOW_CHOOSE_IMAGE_WHEN_ENTER_FLAG_BOOLEAN";
    public final static String PARAM_OCR_OCR_ACTIVITY_AUTO_RETURN_RESULT_FLAG_BOOLEAN = "PARAM_OCR_OCR_ACTIVITY_AUTO_RETURN_RESULT_FLAG_BOOLEAN";
    public final static String PARAM_OCR_OCR_ACTIVITY_RESULT_STRING = "PARAM_OCR_OCR_ACTIVITY_RESULT_STRING";


    //=================================== ocr模块 end =======================================//

    //=========== 成绩模块 start =========== //
    /** 成绩主页 cn.xxt.grade.ui.gradehome.GradeHomeActivity */
    public static final String URL_GRADE_GRADE_HOME_ACTIVITY = "/grade/GradeHomeActivity";

    /** 设置成绩页面 cn.xxt.grade.ui.settinggrade.SettingGradeActivity */
    public static final String URL_GRADE_SETTING_GRADE_ACTIVITY = "/grade/SettingGradeActivity";
    public static final String PARAM_GRADE_SETTING_GRADE_ACTIVITY_GRADE_UNIT_MODEL_LIST = "PARAM_GRADE_SETTING_GRADE_ACTIVITY_GRADE_UNIT_MODEL_LIST";
    public static final String PARAM_GRADE_SETTING_GRADE_ACTIVITY_ORG_NAME_STRING = "PARAM_GRADE_SETTING_GRADE_ACTIVITY_ORG_NAME_STRING";


    /** 成绩发布页面 cn.xxt.grade.ui.gradepublish.GradePublishActivity */
    public static final String URL_GRADE_GRADE_PUBLISH_ACTIVITY = "/grade/GradePublishActivity";
    public static final String PARAM_GRADE_GRADE_PUBLISH_ACTIVITY_GRADE_INFO_SERIALIZABLE = "PARAM_GRADE_GRADE_PUBLISH_ACTIVITY_GRADE_INFO_SERIALIZABLE";


    /** 设置成绩页面，底部弹窗 cn.xxt.grade.ui.settinggrade.bottomdialogfragment.BottomDialogFragment */
    public final static String URL_GRADE_BOTTOM_DIALOG_FRAGMENT = "/grade/BottomDialogFragment";
    public final static String PARAM_GRADE_BOTTOM_DIALOG_FRAGMENT_SETTING_TYPE_INT = "PARAM_GRADE_BOTTOM_DIALOG_FRAGMENT_SETTING_TYPE_INT";
    public final static String PARAM_GRADE_BOTTOM_DIALOG_FRAGMENT_SETTING_MODEL_LIST = "PARAM_GRADE_BOTTOM_DIALOG_FRAGMENT_SETTING_MODEL_LIST";
    public final static String PARAM_GRADE_BOTTOM_DIALOG_FRAGMENT_INPUT_OTHER_EXAM_TYPE_STRING = "PARAM_GRADE_BOTTOM_DIALOG_FRAGMENT_INPUT_OTHER_EXAM_TYPE_STRING";

    // =========== 成绩模块 end =========== //

}
