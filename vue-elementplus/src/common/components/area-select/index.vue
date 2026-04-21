<template>
  <el-cascader
    v-model="selectedValue"
    :options="processedData"
    :props="cascaderProps"
    clearable
    placeholder="请选择省市区"
  />
</template>

<script setup>
import { defineProps, defineEmits, computed, ref, watch, onMounted } from 'vue'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  dataSource: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['update:modelValue'])

const selectedValue = ref([...props.modelValue])

// 配置级联选择器属性
const cascaderProps = {
  value: 'value',
  label: 'label',
  children: 'children',
  checkStrictly: true
}

// 处理外部传入的数据
const processedData = computed(() => {
  return props.dataSource.map(province => ({
    value: province.value,
    label: province.label,
    children: province.children.map(city => ({
      value: city.value,
      label: city.label,
      children: city.children || []
    }))
  }))
})

// 监听值变化
watch(selectedValue, (newVal) => {
  emit('update:modelValue', newVal)
})
</script>