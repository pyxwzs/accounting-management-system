<template>
	<div ref="chartRef"></div>
</template>

<script setup>
	import {
		ref,
		watch,
		reactive,
		nextTick,
		onMounted,
		onUnmounted
	} from "vue";

	// 引入echarts
	import * as echarts from 'echarts'
	import 'echarts-wordcloud'
	import 'echarts/extension/bmap/bmap'

	import utils from "@/utils/utils.js";
	//echarts
	const $echarts = echarts;

	//外部参数
	const option = defineProps({
		//加载详情数据的url地址
		chartOption: {
			type: Object,
			require: true
		}
	});
	watch(() => option.chartOption, (newVal,oldVal) => {
		nextTick(() => {
			chart && chart.setOption(option.chartOption, false);
		});
	}, {
		deep: true
	})
	//dom元素
	const chartRef = ref();
	let chart = null;

	onMounted(() => {
		chart = echarts.init(chartRef.value);
		chart.setOption(option.chartOption);

		window.addEventListener('resize',()=>{
			nextTick(() => {
				chart.resize();
			});
		})
	})
	onUnmounted(()=>{
		// window.removeEventListener("resize");
	})
	
</script>

<style>
</style>
