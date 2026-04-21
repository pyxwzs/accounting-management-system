import {ElMessage,ElLoading,ElMessageBox} from 'element-plus'

Date.prototype.format = function(fmt) {
	var o = {
		"M+": this.getMonth() + 1, //月份 
		"D+": this.getDate(), //日 
		"H+": this.getHours(), //小时 
		"m+": this.getMinutes(), //分 
		"s+": this.getSeconds(), //秒 
		"q+": Math.floor((this.getMonth() + 3) / 3), //季度 
		"S": this.getMilliseconds() //毫秒 
	};
	if (/(Y+)/.test(fmt)) {
		fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	}
	for (var k in o) {
		if (new RegExp("(" + k + ")").test(fmt)) {
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k])
				.length)));
		}
	}
	return fmt;
}

/**
 * 工具方法的定义
 */
const utils = {
	//加载动画
	curLoadding: null,
	//数据加载计数器
	loaddingCount:0,
	showLoadding(msg) {
		utils.loaddingCount ++;
		if (utils.curLoadding) {
			return;
		}
		utils.curLoadding = ElLoading.service({
			body: true,
			fullscreen: true,
			background: 'rgba(0, 0, 0, 0.7)',
			text: msg ? msg : '正在处理中……'
		});
	},
	hideLoadding() {
		utils.loaddingCount --;
		if(utils.loaddingCount<=0){
			utils.curLoadding && utils.curLoadding.close();
			utils.curLoadding = null;
		}
	},
	//消息提示
	/**
	 * 关闭所有消息提示
	 */
	closeMsg() {
		ElMessage.closeAll()
	},
	/**
	 * 展示异常的消息提示
	 * @param {Object} msg 提示的消息内容
	 */
	showError(msg) {
		return ElMessage({
			message: msg,
			grouping: true,
			customClass:"message-info",
			type: 'error',
		})
	},
	/**
	 * 展示成功的消息提示
	 * @param {Object} msg 提示的消息内容
	 */
	showSucess(msg) {
		return ElMessage({
			message: msg,
			grouping: true,
			type: 'success',
		})
	},
	/**
	 * 展示警告的消息提示
	 * @param {Object} msg 提示的消息内容
	 */
	showWarning(msg) {
		return ElMessage({
			message: msg,
			grouping: true,
			type: 'warning',
		})
	},
	/**
	 * 展示提示的消息提示
	 * @param {Object} msg 提示的消息内容
	 */
	showInfo(msg) {
		return ElMessage({
			message: msg,
			grouping: true,
			type: 'info'
		})
	},
	alert(title, message, callback) {
		ElMessageBox.alert(message, title, {
			confirmButtonText: '确定',
			type: 'info',
			center: true,
			autofocus: true,
			showCancelButton: false,
			closeOnClickModal: false,
			closeOnPressEscape: false,
			closeOnHashChange: false,
			showClose: false,
			callback: callback
		})
	},
	confirm(title, message, callback) {
		ElMessageBox.confirm(
				message,
				title ? title : '确认', {
					confirmButtonText: '确定',
					type: 'warning',
					center: true
				}
			)
			.then(() => {
				callback && callback('ok');
			})
			.catch(() => {
				callback && callback('canceled');
			})
	}
};
export default utils;
