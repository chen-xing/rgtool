package net.gzcx.seo;

import com.google.common.collect.Lists;
import com.timevale.mandarin.base.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import net.gzcx.seo.webmagic.ChinazModelPipeline;
import net.gzcx.utils.CommonUtils;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import net.gzcx.domain.TBaiduSid;
import net.gzcx.mapper.TBaiduSidMapper;
import net.gzcx.seo.webmagic.ChinazUrlBean;
import net.gzcx.utils.JarToolUtil;
import net.gzcx.utils.MybatisUtil;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.OOSpider;

import javax.swing.*;
import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author chen xing
 * @description seo助手
 * @date 2022-02-09 10:12
 */
@Slf4j
public class SeoAssistant extends JPanel {
    private JCheckBox ckBaidu = new JCheckBox("百度", true);
    //    private JCheckBox ckGoogle = new JCheckBox("google", false);
    private JComboBox runCountcmb = new JComboBox(); // 执行的次数

    private JButton btnRun = new JButton("执行"); // 开始执行

    private JButton btnGetBaiduSid = new JButton("获取百度SID"); // 开始执行

    private JTextArea txtResult = new JTextArea(); // 输出执行的结果

    /**
     * @author chen xing
     * @description 定义一个线程池
     * @param null
     * @return
     * @date 2022-02-09 14:14
     */
    private ExecutorService service =
            Executors.newCachedThreadPool(
                    new ThreadFactory() {
                        @Override
                        public Thread newThread(Runnable r) {
                            return new Thread(r, "output");
                        }
                    });

    public SeoAssistant() {
        super(new BorderLayout());
        initGUI();
        initListeners();
    }

    private void initGUI() {
        // init sub coms
        txtResult.setLineWrap(true);
        btnRun.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.red));
        btnRun.setForeground(Color.white);
        btnGetBaiduSid.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
        btnGetBaiduSid.setForeground(Color.white);

        // init btn pane
        JPanel btnPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JPanel leftBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        leftBtnPanel.setBorder(BorderFactory.createTitledBorder("引擎"));
        leftBtnPanel.add(ckBaidu);
        //        leftBtnPanel.add(ckGoogle);

        btnPane.add(leftBtnPanel);

        JPanel rightBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rightBtnPanel.setBorder(BorderFactory.createTitledBorder("执行"));

        runCountcmb.addItem(1);
        runCountcmb.addItem(2);
        runCountcmb.addItem(4);
        runCountcmb.addItem(8);
        runCountcmb.addItem(16);

        rightBtnPanel.add(runCountcmb);
        rightBtnPanel.add(btnRun);
        rightBtnPanel.add(btnGetBaiduSid);
        btnPane.add(rightBtnPanel);

        // init main ui
        this.add(btnPane, BorderLayout.NORTH);
        this.add(new JScrollPane(txtResult), BorderLayout.CENTER);
        this.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
    }

    private void initListeners() {
        btnRun.addActionListener(
                e -> {
                    service.submit(
                            () -> {
                                btnRun.setEnabled(false);
                                Integer count =
                                        Integer.parseInt(runCountcmb.getSelectedItem().toString());
                                for (int i = 0; i < count; i++) {
                                    try {
                                        runJob();
                                        TimeUnit.SECONDS.sleep(60);
                                    } catch (InterruptedException ex) {
                                        log.error("system error", ex);
                                    }
                                }
                                btnRun.setEnabled(true);
                                String content = "任务已经完成";
                                this.txtResult.append(content + "\n");
                                txtResult.setCaretPosition(txtResult.getDocument().getLength());
                            });
                });

        btnGetBaiduSid.addActionListener(
                e -> {
                    btnGetBaiduSid.setEnabled(false);
                    Site site = Site.me().setRetryTimes(3).setSleepTime(10000).setTimeOut(6000);
                    OOSpider.create(site, new ChinazModelPipeline(), ChinazUrlBean.class)
                            .addUrl("http://seo.chinaz.com/")
                            .thread(1)
                            .run();
                    btnGetBaiduSid.setEnabled(true);
                });
    }

    // region 私有方法
    /**
     * @author chen xing
     * @description 执行任务
     * @param
     * @return void
     * @date 2022-02-09 10:31
     */
    private void runJob() {
        //        final String targetUrl = "https://www.94rg.com";
        //        final String referUrl =
        //
        // "https://www.baidu.com/link?url=dKTs8TpNPIBxHA0FzBvFYWcMmRaO0U1JVth3YfAEmT3duPGgfX5TB-wNCTRG8tWz&wd=&eqid=ad168783000bcb1e000000045d3a5a9f";

        final String targetUrl =
                "https://www.baidu.com/link?url=dKTs8TpNPIBxHA0FzBvFYWcMmRaO0U1JVth3YfAEmT3duPGgfX5TB-wNCTRG8tWz&wd=&eqid=ad168783000bcb1e000000045d3a5a9f";
        final String referUrl = "https://www.94rg.com";

        String baseDir = JarToolUtil.getJarDir();
        String filePath = baseDir + "/baiduID.csv";
        List<String> baiduIdList = CommonUtils.getListFromCsv(filePath);

        // 同时查询数据2个累计
        TBaiduSidMapper mapper = MybatisUtil.getSqlSession().getMapper(TBaiduSidMapper.class);
        List<TBaiduSid> tBaiduSids = mapper.queryAll();
        List<String> collect =
                Optional.ofNullable(tBaiduSids).orElse(Lists.newArrayList()).stream()
                        .map(
                                x -> {
                                    return x.getSid();
                                })
                        .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(collect)) {
            baiduIdList.addAll(collect);
        }
        log.info("filePath is {} and resultSize is {}", filePath, baiduIdList.size());
        for (String baiduId : baiduIdList) {
            try {
                submitBaiduTj(baiduId, targetUrl, referUrl);
                // 内部暂停一下，防止被百度封杀
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                log.error("执行异常", e);
            } finally {
                String content = MessageFormat.format("baiduId:{0},完成了执行", baiduId);
                this.txtResult.append(content + "\n");
                txtResult.setCaretPosition(txtResult.getDocument().getLength());
                log.info(content);
            }
        }
    }

    /**
     * 提交百度统计
     *
     * @param baiduId
     * @param targetUrl
     */
    private void submitBaiduTj(String baiduId, String targetUrl, String referUrl)
            throws UnsupportedEncodingException {
        BaiduSeo baiduSeo = new BaiduSeo(baiduId, referUrl, targetUrl);
        baiduSeo.run();
    }
    // endregion 结束

    public static void main(String[] args) {
        new SeoAssistant().runJob();
    }
}
