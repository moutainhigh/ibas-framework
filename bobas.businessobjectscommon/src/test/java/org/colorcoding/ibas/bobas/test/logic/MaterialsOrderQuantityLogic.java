package org.colorcoding.ibas.bobas.test.logic;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.logic.BusinessLogic;
import org.colorcoding.ibas.bobas.logic.BusinessLogicException;
import org.colorcoding.ibas.bobas.mapping.LogicContract;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.test.bo.IMaterials;
import org.colorcoding.ibas.bobas.test.bo.Materials;
import org.colorcoding.ibas.bobas.test.repository.BORepositoryTest;

/**
 * 物料订购数量逻辑
 * 
 * @author Niuren.Zhu
 *
 */
@LogicContract(IMaterialsOrderQuantityContract.class)
public class MaterialsOrderQuantityLogic extends BusinessLogic<IMaterialsOrderQuantityContract, IMaterials> {

	@Override
	protected IMaterials fetchBeAffected(IMaterialsOrderQuantityContract contract) {
		ICriteria criteria = Criteria.create();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(Materials.PROPERTY_ITEMCODE.getName());
		condition.setValue(contract.getItemCode());
		// 先在事务缓存中查询
		IMaterials materials = this.fetchBeAffected(criteria, IMaterials.class);
		if (materials == null) {
			// 事务中不存在
			BORepositoryTest boRepositry = new BORepositoryTest();
			boRepositry.setRepository(this.getRepository());
			IOperationResult<IMaterials> operationResult = boRepositry.fetchMaterials(criteria);
			if (operationResult.getError() != null) {
				throw new BusinessLogicException(operationResult.getError());
			}
			materials = operationResult.getResultObjects().firstOrDefault();
		}
		if (materials == null) {
			throw new RuntimeException(String.format("物料[%s]不存在。", contract.getItemCode()));
		}
		return materials;
	}

	@Override
	protected void impact(IMaterialsOrderQuantityContract contract) {
		// 增加订购数量
		this.getBeAffected().setOnOrder(this.getBeAffected().getOnOrder().add(contract.getQuantity()));
		Logger.log("logic: %s's order quantity add %s.", this.getBeAffected(), contract.getQuantity());
	}

	@Override
	protected void revoke(IMaterialsOrderQuantityContract contract) {
		// 减小订购数量
		this.getBeAffected().setOnOrder(this.getBeAffected().getOnOrder().subtract(contract.getQuantity()));
		Logger.log("logic: %s's order quantity sub %s.", this.getBeAffected(), contract.getQuantity());
	}

}
