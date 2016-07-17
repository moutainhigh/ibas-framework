package org.colorcoding.ibas.bobas.test.logics;

import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.repository.BORepository4File;
import org.colorcoding.ibas.bobas.repository.DataCacheUsage;
import org.colorcoding.ibas.bobas.repository.IBORepository4File;
import org.colorcoding.ibas.bobas.test.bo.IMaterials;
import org.colorcoding.ibas.bobas.test.bo.Materials;
import org.colorcoding.ibas.bobas.test.repository.BORepositoryTest;

import junit.framework.TestCase;

public class testLogics extends TestCase {

	public void testMaterialsOrderQuantity() {
		// 创建物料数据
		IMaterials materials01 = new Materials();
		materials01.setItemCode(String.format("A%s", DateTime.getNow().getTime()));
		materials01.setItemDescription("CPU i7");
		IMaterials materials02 = new Materials();
		materials02.setItemCode(String.format("B%s", DateTime.getNow().getTime()));
		materials02.setItemDescription("Disk 5T");
		// 保存物料到文件系统
		IBORepository4File fileRepository = new BORepository4File();
		// fileRepository.setRepositoryFolder("D:\\WorkTemp\\borepository");
		BORepositoryTest boRepository = new BORepositoryTest();
		boRepository.setDataCacheUsage(DataCacheUsage.NOT_USE);// 缓存会导致数据检索不到
		boRepository.setRepository(fileRepository);
		IOperationResult<?> operationResult;

		operationResult = boRepository.saveMaterials(materials01);
		assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
		operationResult = boRepository.saveMaterials(materials02);
		assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);

		// 创建采购订单
		PurchaseOrder order = new PurchaseOrder();
		order.setCustomerCode("C00001");
		order.setCustomerName("宇宙无敌影业");
		PurchaseOrderItem item01 = order.getPurchaseOrderItems().create();
		item01.setItemCode(materials01.getItemCode());
		item01.setItemDescription(materials01.getItemDescription());
		item01.setQuantity(1);
		item01.setPrice(999999.99);
		PurchaseOrderItem item02 = order.getPurchaseOrderItems().create();
		item02.setItemCode(materials02.getItemCode());
		item02.setItemDescription(materials02.getItemDescription());
		item02.setQuantity(2);
		item02.setPrice(99.00);
		PurchaseOrderItem item03 = order.getPurchaseOrderItems().create();
		item03.setItemCode(materials02.getItemCode());
		item03.setItemDescription(materials02.getItemDescription());
		item03.setQuantity(9);
		item03.setPrice(99.00);

		operationResult = boRepository.savePurchaseOrder(order);
		if (operationResult.getResultCode() != 0) {
			System.err.println(operationResult.getMessage());
		}
		assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);

		operationResult = boRepository.fetchMaterials(materials01.getCriteria());
		assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
		IMaterials materials01s = (IMaterials) operationResult.getResultObjects().firstOrDefault();
		// 检索的物料是否一致
		assertEquals("materials not same.", materials01.getItemCode(), materials01s.getItemCode());
		// 订购数量是否增加
		assertEquals(String.format("wrong matrials [%s] order quantity.", materials01.getItemCode()),
				materials01.getOnOrder().add(item01.getQuantity()).floatValue(),
				materials01s.getOnOrder().floatValue());
		operationResult = boRepository.fetchMaterials(materials02.getCriteria());
		assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
		IMaterials materials02s = (IMaterials) operationResult.getResultObjects().firstOrDefault();
		assertEquals("materials not same.", materials02.getItemCode(), materials02s.getItemCode());
		assertEquals(String.format("wrong matrials [%s] order quantity.", materials02.getItemCode()),
				materials02.getOnOrder().add(item02.getQuantity().add(item03.getQuantity())).floatValue(),
				materials02s.getOnOrder().floatValue());

		// 修改数量

		item01.setQuantity(20);
		operationResult = boRepository.savePurchaseOrder(order);
		assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);

		operationResult = boRepository.fetchMaterials(materials02.getCriteria());
		assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
		materials02s = (IMaterials) operationResult.getResultObjects().firstOrDefault();
		assertEquals("materials not same.", materials02.getItemCode(), materials02s.getItemCode());
		assertEquals(String.format("wrong matrials [%s] order quantity.", materials02.getItemCode()),
				materials02.getOnOrder().add(item02.getQuantity().add(item03.getQuantity())).floatValue(),
				materials02s.getOnOrder().floatValue());

		operationResult = boRepository.fetchMaterials(materials01.getCriteria());
		assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
		materials01s = (IMaterials) operationResult.getResultObjects().firstOrDefault();
		assertEquals(String.format("wrong matrials [%s] order quantity.", materials01.getItemCode()),
				materials01.getOnOrder().add(item01.getQuantity()).floatValue(),
				materials01s.getOnOrder().floatValue());

	}
}
