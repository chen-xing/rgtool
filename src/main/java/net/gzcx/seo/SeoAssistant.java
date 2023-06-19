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
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.OOSpider;

import javax.swing.*;
import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.Random;
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

    private JCheckBox jCheckBox = new JCheckBox("无图模式");

    private JLabel jLabelSleep = new JLabel("停顿事件(s)");
    private JLabel jLabelExecuteTime = new JLabel("浏览的页面数");
    private JComboBox sleepTimeCom = new JComboBox();
    private JComboBox executeTime = new JComboBox();
    private JButton btnsimulate = new JButton("模拟访问"); // 开始执行

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

        JPanel seleniumPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        seleniumPanel.setBorder(BorderFactory.createTitledBorder("模拟访问"));

        seleniumPanel.add(jCheckBox);
        seleniumPanel.add(jLabelSleep);

        sleepTimeCom.addItem("1");
        sleepTimeCom.addItem("5");
        sleepTimeCom.addItem("10");
        sleepTimeCom.addItem("60");
        sleepTimeCom.addItem("180");
        seleniumPanel.add(sleepTimeCom);
        seleniumPanel.add(jLabelExecuteTime, BorderLayout.WEST);

        executeTime.addItem("10");
        executeTime.addItem("30");
        executeTime.addItem("50");
        executeTime.addItem("100");
        seleniumPanel.add(executeTime);

        btnsimulate.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
        btnsimulate.setForeground(Color.white);
        seleniumPanel.add(btnsimulate);

        btnPane.add(seleniumPanel);

        JPanel leftBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        leftBtnPanel.setBorder(BorderFactory.createTitledBorder("引擎"));
        leftBtnPanel.add(ckBaidu);
        //        leftBtnPanel.add(ckGoogle);

        btnPane.add(leftBtnPanel, BorderLayout.CENTER);

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
        btnPane.add(rightBtnPanel, BorderLayout.EAST);

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

        btnsimulate.addActionListener(
                e -> {
                    new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            try {
                                startEdge(
                                        Integer.parseInt(sleepTimeCom.getSelectedItem().toString()),
                                        Integer.parseInt(executeTime.getSelectedItem().toString()),
                                        jCheckBox.isSelected());
                            } catch (InterruptedException ex) {
                                log.warn("执行模拟访问异常", ex);
                            }
                            return null;
                        }
                    }.execute();
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
        final String referUrl = "https://www.gzcx.net";

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

    /**
     * @author chen xing
     * @description 执行浏览器的模拟访问
     * @param sleepTime 停顿的时间
     * @param executeTime 执行的次数
     * @param graphlessMode 开启无图模式操作
     * @return void
     * @date 2023-06-16 13:38
     */
    private void startEdge(Integer sleepTime, Integer executeTime, boolean graphlessMode)
            throws InterruptedException {
        ChromeOptions options = new ChromeOptions(); // 创建浏览器参数
        // 设置从ChromeDriver中获取属性（处理反爬机制）
        // 设置谷歌浏览器用户数据目录

        // 执行无图浏览器的操作
        if (graphlessMode) {
            options.addArguments("--headless");
        }
        options.addArguments("--disable-gpu");
        options.addArguments("start-maximized");
        options.addArguments("enable-automation");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-browser-side-navigation");

        // 设置 Chrome driver 的路径
        System.setProperty("webdriver.chrome.driver", "E:\\chenxing\\tools\\chromedriver.exe");

        // 初始化 Chrome driver
        WebDriver driver = new ChromeDriver(options);

        String url = getStartPage();
        // 打开网页
        driver.get(url);

        for (int m = 0; m < executeTime; m++) {
            TimeUnit.SECONDS.sleep(sleepTime);
            WebElement webElement = driver.findElement(By.cssSelector("body"));
            webElement.click(); // 有的时候必须点击一下，下拉才能生效（有的网站是这样，原因未找到）

            for (int i = 0; i < 5; i++) {
                webElement.sendKeys(Keys.PAGE_DOWN);
                TimeUnit.SECONDS.sleep(3);
            }

            WebElement element =
                    driver.findElement(By.className("line-container")).findElement(By.tagName("a"));
            Actions actions = new Actions(driver);
            actions.moveToElement(element).click().perform();
        }

        // 关闭浏览器
        driver.quit();
    }

    private String getStartPage() {
        int i = new Random().nextInt(18);
        if (i == 0) {
            i = 1;
        }
        return String.format("https://www.gzcx.net/article/%d", i);
    }

    public static void main(String[] args) {
        new SeoAssistant().runJob();
    }
}
