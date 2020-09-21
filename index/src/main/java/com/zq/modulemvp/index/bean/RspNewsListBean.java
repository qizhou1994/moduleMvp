package com.zq.modulemvp.index.bean;

import java.util.List;

/**
 * desc
 * author zhouqi
 * data 2020/6/10
 */
public class RspNewsListBean {


    /**
     * result : [{"title":"俄商店售丑化奥巴马切菜板遭美抗议 商家道歉(图)","content":"据法新社12月10日报道称,俄罗斯一连锁店销售的切菜板上,出现了一个猴子家庭,而其中最小的猴子脸是奥巴马的面孔。一名顾客在购买了这款切菜板,并拍下照片。这些照片被疯传,引爆了这起丑闻。  美驻俄使馆发言人威尔·史蒂文斯在推特上发帖说:\"看到这样公然的种族主义表现出现...","img_width":"342","full_title":"俄商店售丑化奥巴马切菜板遭美抗议 商家道歉(图)","pdate":"43分钟前","src":"中国青年网","img_length":"630","img":"http://p5.qhimg.com/t01664dcf8e83665742.jpg","url":"http://news.youth.cn/gj/201512/t20151213_7413620.htm","pdate_src":"2015-12-13 15:39:28"},{"title":"奥巴马称全球气候新协议为解决气候危机建立了\"持久框架\"","content":"奥巴马当天还为美国的减排政策辩护,表示过去7年来美国一直致力于抗击气候变化,大力发展风能和太阳能等产业,为本国带来全新、稳定的中产阶级就业岗位,并首次为全国的发电厂碳排放设定了标准。一些批评意见认为上述举措将扼杀就业,但事实并非如此,美国私营部门正创造更多就...","img_width":"","full_title":"奥巴马称全球气候新协议为解决气候危机建立了\"持久框架\"","pdate":"7小时前","src":"中国西藏网","img_length":"","img":"","url":"http://www.tibet.cn/news/expo/1449969320589.shtml","pdate_src":"2015-12-13 09:14:17"},{"title":"受美驻俄使馆痛批 俄零售商下架\"奥巴马砧板\"","content":"乌克兰危机爆发后,美俄矛盾激化,两国关系滑至冷战后新低,奥巴马的形象也时常在俄罗斯遭到恶搞。比如,前花样滑冰奥运冠军、现统一俄罗斯党议员伊琳娜·罗德宁娜的推特账户2013年出现一幅图片,内容为奥巴马和妻子米歇尔正盯着一只香蕉。罗德宁娜稍后删除了这张图片,说图片是...","img_width":"","full_title":"受美驻俄使馆痛批 俄零售商下架\"奥巴马砧板\"","pdate":"9小时前","src":"中国网","img_length":"","img":"","url":"http://news.china.com.cn/live/2015-12/13/content_34809728.htm","pdate_src":"2015-12-13 06:50:44"},{"title":"习近平同美国总统奥巴马通电话","content":"新华社北京12月11日电 国家主席习近平12月11日应约同美国总统奥巴马通电话。  习近平指出,不久前,我们在巴黎举行会晤,就中美关系和共同关心的问题深入交换意见,达成不少新共识。新的一年即将开始,中美关系面临重要发展机遇,也存在一些挑战。保持中美关系持续健康稳定发展,...","img_width":"","full_title":"习近平同美国总统奥巴马通电话","pdate":"1天前","src":"党建网","img_length":"","img":"","url":"http://www.dangjian.cn/gcsy/yw/201512/t20151212_3016972.shtml","pdate_src":"2015-12-12 10:07:16"}]
     * error_code : 0
     * reason : Succes
     */

    private int error_code;
    private String reason;
    private List<ResultBean> result;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * title : 俄商店售丑化奥巴马切菜板遭美抗议 商家道歉(图)
         * content : 据法新社12月10日报道称,俄罗斯一连锁店销售的切菜板上,出现了一个猴子家庭,而其中最小的猴子脸是奥巴马的面孔。一名顾客在购买了这款切菜板,并拍下照片。这些照片被疯传,引爆了这起丑闻。  美驻俄使馆发言人威尔·史蒂文斯在推特上发帖说:"看到这样公然的种族主义表现出现...
         * img_width : 342
         * full_title : 俄商店售丑化奥巴马切菜板遭美抗议 商家道歉(图)
         * pdate : 43分钟前
         * src : 中国青年网
         * img_length : 630
         * img : http://p5.qhimg.com/t01664dcf8e83665742.jpg
         * url : http://news.youth.cn/gj/201512/t20151213_7413620.htm
         * pdate_src : 2015-12-13 15:39:28
         */

        private String title;
        private String content;
        private String img_width;
        private String full_title;
        private String pdate;
        private String src;
        private String img_length;
        private String img;
        private String url;
        private String pdate_src;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getImg_width() {
            return img_width;
        }

        public void setImg_width(String img_width) {
            this.img_width = img_width;
        }

        public String getFull_title() {
            return full_title;
        }

        public void setFull_title(String full_title) {
            this.full_title = full_title;
        }

        public String getPdate() {
            return pdate;
        }

        public void setPdate(String pdate) {
            this.pdate = pdate;
        }

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public String getImg_length() {
            return img_length;
        }

        public void setImg_length(String img_length) {
            this.img_length = img_length;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPdate_src() {
            return pdate_src;
        }

        public void setPdate_src(String pdate_src) {
            this.pdate_src = pdate_src;
        }
    }
}
