package com.yibasan.fm.routerapp;


/**
 * Router
 * author IDBA
 * email radio.ysh@qq.com
 * created 2017-10-19 14:47
 * describe:
 **/
public class ThreadTest {

    /**
     * �۲�ֱ�ӵ���run()����start()����һ���̵߳Ĳ��
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) {
        Thread thread = new ThreadDemo();
        //��һ��
        //����: run()�����������ĵ���û�κβ�ͬ,main������˳��ִ������,����ӡ�����һ��
        thread.run();

        //�ڶ���
        //����: start()�������´�����һ���߳�,��main����ִ�н�����,����start()�����������߳�û�����н���,
        //������߳�δ���˳�,ֱ���߳�threadҲִ�����.����Ҫע��,Ĭ�ϴ������߳����û��߳�(���ػ��߳�)
//        thread.start();

        //������
        //1��Ϊʲôû�д�ӡ��100����?��Ϊ���ǽ�thread�߳�����Ϊ��daemon(�ػ�)�߳�,������ֻ���ػ��̴߳��ڵ�ʱ��,�ǿ����˳���,����ֻ��ӡ���߾���˳���
        //2����java����������ػ��߳������е�ʱ��java�������رա������г����߳���������Ժ�
        //�ػ��̲߳������е��������������˳����С���������ػ��߳���ò�ҪдһЩ��Ӱ������ҵ���߼��������޷�Ԥ�ϳ��򵽵׻����ʲô����
//        thread.setDaemon(true);
//        thread.start();

        //������
        //�û��߳̿��Ա�System.exit(0)ǿ��kill��,����Ҳֻ��ӡ���߾�
//        thread.start();
        System.out.println("main thread is over");
//        System.exit(1);
    }

    public static class ThreadDemo extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                System.out.println("This is a Thread test" + i);
            }
        }
    }
}

