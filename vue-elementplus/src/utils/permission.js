import store from '@/store'
/**
 * 
 */
const permission = (el, binding, vnode) => {
	//与后端返回的数组进行比对，如果后端返回的数组中的值包含binding.value
	//从后台加载数据判断是否能进行相应操作
	//当前登录用户的信息
	const curUserInfo = store.getters.getUserInfo;
	let hasPermission = false;
	curUserInfo.permission.forEach((item, index) => {
		if (item.entity == binding.value.entityType) {
			if (item.permissions) {
				for (let i = 0; i < item.permissions.length; i++) {
					if (item.permissions[i] == binding.value.key) {
						hasPermission = true;
						break;
					}
				}
			}
		}
	});
	//则有权限，该按钮展示，否则隐藏
	if (!hasPermission) {
		if(el.className.indexOf("el-button")!=-1){
			el.setAttribute("disabled","disabled");
			el.setAttribute("aria-disabled","true");
			el.className=el.className+" is-disabled"
		}else{
			el.parentNode && el.parentNode.removeChild(el);
		}
		return;
	}
	if (binding.value.data && binding.value.data.permissions) { //有具体数据，则标识需要判断此数据当用户是否具有操作权限
		// 1.调用权限检测接口，判断用户是否具有该数据的权限
		//2.数据中本身带有对应的权限标识
		hasPermission = false;
		for (let i = 0; i < binding.value.data.permissions.length; i++) {
			if (binding.value.data.permissions[i] == binding.value.key) {
				hasPermission = true;
				break;
			}
		}
	}
	if (!hasPermission) {
		if(el.className.indexOf("el-button")!=-1){
			el.setAttribute("disabled","disabled");
			el.setAttribute("aria-disabled","true");
			el.className=el.className+" is-disabled"
		}else{
			el.parentNode && el.parentNode.removeChild(el);
		}
	}
}
export default permission;
