package org.cola.GuradCelia;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.apache.log4j.PropertyConfigurator;
import org.cola.GuradCelia.cmdhandler.CmdHandlerFactory;
import org.cola.GuradCelia.mq.MqProducer;
import org.cola.GuradCelia.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 前端网站地址
 * http://cdn0001.afrxvk.cn/hero_story/demo/step040/index.html?serverAddr=127.0.0.1:12345&userId=1
 *
 * protobuf 语句
 *  protoc --java_out=.\ .\GameMsgProtocol.proto
 *
 */
public class ServerMain {
    //日志对象
    static private final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);

    public static void main(String[] args) {
        // 设置 log4j 属性文件
        PropertyConfigurator.configure(ServerMain.class.getClassLoader().getResourceAsStream("log4j.properties"));

        // 初始化命令处理器工厂
        CmdHandlerFactory.init();
        // 初始化消息识别器
        GameMsgRecognizer.init();
        // 初始化 MySql 会话工厂
        MySqlSessionFactory.init();
        // 初始化 Redis
        RedisUtil.init();
        // 初始化消息队列
        MqProducer.init();

        EventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, worker);
        serverBootstrap.channel(NioServerSocketChannel.class);  // 服务器信道的处理方式
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(
                        new HttpServerCodec(),    // Http 服务器编解码器
                        new HttpObjectAggregator(65535),  // 内容长度限制
                        new WebSocketServerProtocolHandler("/websocket"),  // WebSocket 协议处理器, 在这里处理握手、ping、pong 等消息
                        new GameMsgDecoder(),   //自定义的消息解码器
                        new GameMsgEncoder(),   //自定义的消息编码器
                        new GameMsgHandler()  //自定义的消息处理器
                );
            }
        });

        serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

        try {
            ChannelFuture future = serverBootstrap.bind(12345).sync();
            if (future.isSuccess()) {
                LOGGER.info("游戏服务器启动成功");
            }

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }

    }
}
