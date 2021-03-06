package io.pivotal.gemfire.demo.server.customerorder.function;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.ResultCollector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.pivotal.gemfire.demo.model.gf.key.CustomerKey;
import io.pivotal.gemfire.demo.model.gf.key.CustomerOrderKey;
import io.pivotal.gemfire.demo.model.gf.key.ItemKey;
import io.pivotal.gemfire.demo.model.gf.pdx.Customer;
import io.pivotal.gemfire.demo.model.gf.pdx.CustomerOrder;
import io.pivotal.gemfire.demo.model.gf.pdx.Item;
import io.pivotal.gemfire.demo.model.io.CustomerOrderIO;
import io.pivotal.gemfire.demo.server.config.GemFireCustomerOrderServerConfig;
import io.pivotal.gemfire.demo.server.customerorder.TestUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { GemFireCustomerOrderServerConfig.class })
public class CustomerOrderListFunctionIntegrationTest {
	@Autowired
	private TestUtil testUtil;

	@Resource(name = "customer")
	private Region<CustomerKey, Customer> customerRegion;

	@Resource(name = "customer-order")
	private Region<CustomerOrderKey, CustomerOrder> customerOrderRegion;

	@Resource(name = "item")
	private Region<ItemKey, Item> itemRegion;

	@Before
	public void setUp() throws Exception {
		testUtil.loadGemFireData();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testExecute() {
		CustomerKey customerKey = new CustomerKey("customer1");
		Set<CustomerKey> filter = new HashSet<CustomerKey>();
		filter.add(customerKey);

		Execution execution = FunctionService.onRegion(customerOrderRegion).withFilter(filter);
		ResultCollector<?, ?> rc = execution.execute(CustomerOrderListFunction.class.getSimpleName());
		List<CustomerOrderIO> result = (List<CustomerOrderIO>) rc.getResult();
		Assert.assertEquals(2, result.size());
		Assert.assertNotNull(result.get(1));
		Assert.assertEquals(customerKey.getId(), result.get(1).getCustomer().getId());

		customerKey = new CustomerKey("customer2");
		filter = new HashSet<CustomerKey>();
		filter.add(customerKey);

		execution = FunctionService.onRegion(customerOrderRegion).withFilter(filter);
		rc = execution.execute(CustomerOrderListFunction.class.getSimpleName());
		result = (List<CustomerOrderIO>) rc.getResult();
		Assert.assertEquals(1, result.size());
		Assert.assertNotNull(result.get(0));
		Assert.assertEquals(customerKey.getId(), result.get(0).getCustomer().getId());

		customerKey = new CustomerKey("customer3");
		filter = new HashSet<CustomerKey>();
		filter.add(customerKey);

		execution = FunctionService.onRegion(customerOrderRegion).withFilter(filter);
		rc = execution.execute(CustomerOrderListFunction.class.getSimpleName());
		result = (List<CustomerOrderIO>) rc.getResult();
		Assert.assertEquals(1, result.size());
		Assert.assertNull(result.get(0));
	}

}
