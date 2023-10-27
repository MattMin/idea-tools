package com.oeong.tools;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @descriptions:
 * @author: Zzw
 * @date: 2023/10/19 9:42
 */
public class ScreenshotTools extends JFrame {
    private static final long serialVersionUID = 1L;
    int orgx, orgy, endx, endy;
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    BufferedImage image;
    BufferedImage tempImage;
    BufferedImage saveImage;
    Graphics g;
    Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();//获取系统剪贴板

    public ScreenshotTools() {
        snapshot();
        setVisible(true);
        setPreferredSize(d);
        setSize(d);//最大化窗口
        setAlwaysOnTop(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                orgx = e.getX();
                orgy = e.getY();
            }
        });
        //鼠标运动监听器
        this.addMouseMotionListener(new MouseMotionAdapter() {
            //鼠标拖拽事件
            public void mouseDragged(MouseEvent e) {
                endx = e.getX();
                endy = e.getY();
                g = getGraphics();
                g.drawImage(tempImage, 0, 0, ScreenshotTools.this);
                int x = Math.min(orgx, endx);
                int y = Math.min(orgy, endy);
                //加上1，防止width,height为0
                int width = Math.abs(endx - orgx) + 1;
                int height = Math.abs(endy - orgy) + 1;
                g.setColor(Color.BLUE);
                g.drawRect(x - 1, y - 1, width + 1, height + 1);
                //减1，加1都是为了防止图片将矩形框覆盖掉
                saveImage = image.getSubimage(x, y, width, height);
                g.drawImage(saveImage, x, y, ScreenshotTools.this);
            }
        });

        this.addKeyListener(new KeyAdapter() {
            @Override
            //按键释放
            public void keyReleased(KeyEvent e) {
                //按Esc键退出
                if (e.getKeyCode() == 27) {
                    dispose();
                }
                //enter 截图
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    saveToFile();
                    dispose();
                }
            }
        });
    }

    //开始截图
    public static void startScreenshot() {
        //全屏运行
        ScreenshotTools rd = new ScreenshotTools();
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();

        gd.setFullScreenWindow(rd);

        try {
            gd.setFullScreenWindow(rd);
        } catch (Exception e){
            gd.setFullScreenWindow(null);
        }
    }

    /**
     * 复制图片到剪切板。
     */
    public static void setClipboardImage(final Image image) {
        Transferable trans = new Transferable() {
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{DataFlavor.imageFlavor};
            }

            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return DataFlavor.imageFlavor.equals(flavor);
            }

            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                if (isDataFlavorSupported(flavor)) return image;
                throw new UnsupportedFlavorException(flavor);
            }
        };
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
    }

    @Override
    public void paint(Graphics g) {
        //缩放因子和偏移量
        RescaleOp ro = new RescaleOp(0.8f, 0, null);
        tempImage = ro.filter(image, null);
        g.drawImage(tempImage, 0, 0, this);
    }

    public void saveToFile() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddHHmmss");
        String name = sdf.format(new Date());
        File path = FileSystemView.getFileSystemView().getHomeDirectory();
        String format = "jpg";
        File f = new File(path + File.separator + name + "." + format);
        try {
            setClipboardImage(saveImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void snapshot() {

        try {
            Robot robot = new Robot();
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            image = robot.createScreenCapture(new Rectangle(0, 0, d.width, d.height));
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过图片url 将图片复制到剪贴板
     *
     * @param path
     */
    public void imgUrlToClipboard(String path) {
//        URL url;
        try {
//            URL url = new URL("//192.168.2.100:8080/sss/images/copyright.jpg");
            URL url = new URL(path);
            //载入图片到输入流
            java.io.BufferedInputStream bis = new BufferedInputStream(url.openStream());
            BufferedImage bi = ImageIO.read(bis);
            Image im = (Image) bi;
            setClipboardImage(im);
        } catch (Exception e) {

        }
    }

    /**
     * 从指定的剪切板中获取文本内容
     * 本地剪切板使用 Clipborad cp = new Clipboard("clip1"); 来构造
     * 系统剪切板使用 Clipboard sysc = Toolkit.getDefaultToolkit().getSystemClipboard();
     * 剪切板的内容 getContents(null); 返回Transferable
     */
    public static Image getClipboardImage() throws Exception {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();//获取系统剪贴板
        // 获取剪切板中的内容
        Transferable clipT = clip.getContents(null);
        if (clipT != null) {
            // 检查内容是否是文本类型
            if (clipT.isDataFlavorSupported(DataFlavor.imageFlavor))

                return (Image) clipT.getTransferData(DataFlavor.imageFlavor);

        }

        return null;
    }
}
