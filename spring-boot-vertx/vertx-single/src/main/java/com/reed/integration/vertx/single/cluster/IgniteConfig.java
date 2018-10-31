package com.reed.integration.vertx.single.cluster;

import java.util.Arrays;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.slf4j.LoggerFactory;

import io.netty.util.internal.StringUtil;

/**
 * 自定义ignite配置
 * @author reed
 *
 */
public class IgniteConfig {

	public static IgniteConfiguration igniteConfiguration() {
		String nodes = System.getenv("IGNITE_CLUSTER_NODES");
		boolean isMulticast = StringUtil.isNullOrEmpty(nodes);

		TcpDiscoverySpi spi = new TcpDiscoverySpi();
		if (isMulticast) {
			// multicast
			TcpDiscoveryMulticastIpFinder multicastFinder = new TcpDiscoveryMulticastIpFinder();
			spi.setIpFinder(multicastFinder);
			System.out.println("Ignite is working on multicast cluster mode. ");
		} else {
			// static IP Based Discovery, see:
			// https://apacheignite.readme.io/docs/cluster-config#section-static-ip-based-discovery
			String clusterNodes = nodes == null ? "localhost" : nodes;
			TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
			ipFinder.setAddresses(Arrays.asList(clusterNodes.split(",")));
			spi.setIpFinder(ipFinder);
			System.out.println("Ignite is working on ip based cluster mode. " + "Cluster ip list: " + clusterNodes);
		}

		IgniteConfiguration cfg = new IgniteConfiguration();

		// Override default discovery SPI.
		cfg.setDiscoverySpi(spi);
		// deal with logging twice
		cfg.setGridLogger(new Slf4jLogger(LoggerFactory.getLogger("org.apache.ignite")));

		return cfg;
	}
}
