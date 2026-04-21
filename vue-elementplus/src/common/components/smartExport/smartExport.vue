<template>
   <el-dropdown trigger="click" @command="handleExport">
    <el-button type="primary" :icon="Download">导出</el-button>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="excel">
          <el-icon><Document /></el-icon>
          <span>导出Excel</span>
        </el-dropdown-item>
        <el-dropdown-item command="pdf" divided>
          <el-icon><Document /></el-icon>
          <span>导出PDF</span>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
  
  <!-- 隐藏的表格容器，用于生成PDF -->
  <div v-if="pdfGenerating" ref="pdfContainer" class="pdf-container">
    <h2 class="pdf-title">{{ props.title }}</h2>
    <p class="pdf-time">导出时间: {{ currentTime }}</p>
    <table class="pdf-table">
      <thead>
        <tr>
          <th v-for="(col, index) in pdfHeaders" :key="index" class="pdf-th">{{ col }}</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(row, rowIndex) in pdfTableData" :key="rowIndex" class="pdf-tr">
          <td v-for="(cell, cellIndex) in row" :key="cellIndex" class="pdf-td">{{ cell }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import * as XLSX from 'xlsx'
import { jsPDF } from 'jspdf'
import html2canvas from 'html2canvas'
import { Download } from '@element-plus/icons-vue'
import { ElMessage, ElLoading } from 'element-plus'

const props = defineProps({
  data: {
    type: Array,
    required: true,
    default: () => []
  },
  columns: {
    type: Array,
    required: true,
    default: () => []
  },
  fileName: {
    type: String,
    default: '导出数据'
  },
  title: {
    type: String,
    default: '数据列表'
  }
})

const pdfGenerating = ref(false)
const pdfContainer = ref(null)
const currentTime = ref('')

// 处理数据为导出格式
const processData = computed(() => {
  return props.data.map(item => {
    const processedItem = {}
    props.columns.forEach(col => {
      if (col.prop && item[col.prop] !== undefined) {
        processedItem[col.label] = item[col.prop]
      }
    })
    return processedItem
  })
})

// 处理PDF表格数据
const pdfTableData = computed(() => {
  return props.data.map(item => {
    return props.columns
      .filter(col => col.prop)
      .map(col => {
        const value = item[col.prop] || ''
        // 处理超长文本
        if (col.isImage || col.type === 'image') {
          return '图片凭证'
        } else if (typeof value === 'string' && value.length > 30) {
          return value.substring(0, 30) + '...'
        }
        return value
      })
  })
})

// PDF表头
const pdfHeaders = computed(() => {
  return props.columns
    .filter(col => col.prop && col.label)
    .map(col => col.label)
})
const handleExport = (command) => {
  if (command === 'excel') {
    handleExportExcel()
  } else if (command === 'pdf') {
    handleExportPDF()
  }
}
// 导出Excel
const handleExportExcel = () => {
  if (!props.data.length) {
    ElMessage.warning('没有数据可导出')
    return
  }
  
  try {
    const worksheet = XLSX.utils.json_to_sheet(processData.value)
    const workbook = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(workbook, worksheet, props.title)
    XLSX.writeFile(workbook, `${props.fileName}_${formatDate()}.xlsx`)
    ElMessage.success('Excel导出成功')
  } catch (error) {
    console.error('导出Excel失败:', error)
    ElMessage.error('导出失败')
  }
}

// 导出PDF
const handleExportPDF = async () => {
  if (!props.data.length) {
    ElMessage.warning('没有数据可导出')
    return
  }
  
  const loading = ElLoading.service({
    lock: true,
    text: '正在生成PDF...',
    background: 'rgba(0, 0, 0, 0.7)'
  })
  
  try {
    pdfGenerating.value = true
    currentTime.value = new Date().toLocaleString()
    
    // 等待DOM更新
    await nextTick()
    
    // 确保容器已渲染
    if (!pdfContainer.value) {
      throw new Error('PDF容器未找到')
    }
    
    // 设置容器可见以便截图
    pdfContainer.value.style.position = 'fixed'
    pdfContainer.value.style.left = '0'
    pdfContainer.value.style.top = '0'
    pdfContainer.value.style.width = '800px'
    pdfContainer.value.style.zIndex = '9999'
    pdfContainer.value.style.backgroundColor = 'white'
    pdfContainer.value.style.padding = '20px'
    
    // 使用html2canvas生成图片
    const canvas = await html2canvas(pdfContainer.value, {
      scale: 2, // 提高分辨率
      useCORS: true,
      backgroundColor: '#ffffff'
    })
    
    // 隐藏容器
    pdfContainer.value.style.position = 'absolute'
    pdfContainer.value.style.left = '-9999px'
    
    // 计算PDF尺寸
    const imgWidth = 190 // A4纸宽度减边距
    const imgHeight = (canvas.height * imgWidth) / canvas.width
    
    // 创建PDF
    const pdf = new jsPDF('p', 'mm', 'a4')
    
    // 如果图片高度超过A4纸高度，需要分页
    let heightLeft = imgHeight
    let position = 10 // 起始位置
    
    pdf.addImage(canvas, 'PNG', 10, position, imgWidth, imgHeight)
    heightLeft -= 277 // A4纸高度为297mm，留20mm边距
    
    // 如果内容超过一页，继续添加页面
    while (heightLeft > 0) {
      position = heightLeft - imgHeight
      pdf.addPage()
      pdf.addImage(canvas, 'PNG', 10, position, imgWidth, imgHeight)
      heightLeft -= 277
    }
    
    // 保存文件
    pdf.save(`${props.fileName}_${formatDate()}.pdf`)
    
    ElMessage.success('PDF导出成功')
  } catch (error) {
    console.error('导出PDF失败:', error)
    ElMessage.error('导出失败: ' + error.message)
  } finally {
    loading.close()
    pdfGenerating.value = false
  }
}

// 格式化日期用于文件名
const formatDate = () => {
  const now = new Date()
  return `${now.getFullYear()}${(now.getMonth() + 1).toString().padStart(2, '0')}${now.getDate().toString().padStart(2, '0')}_${now.getHours().toString().padStart(2, '0')}${now.getMinutes().toString().padStart(2, '0')}`
}

defineExpose({
  handleExportExcel,
  handleExportPDF
})
</script>

<style scoped>
.pdf-container {
  position: absolute;
  left: -9999px;
  top: 0;
  width: 800px;
  background: white;
  padding: 20px;
  font-family: 'Microsoft YaHei', 'SimSun', sans-serif;
}

.pdf-title {
  text-align: center;
  margin-bottom: 15px;
  color: #333;
  font-size: 20px;
}

.pdf-time {
  text-align: left;
  margin-bottom: 20px;
  color: #666;
  font-size: 14px;
}

.pdf-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
}

.pdf-th {
  background-color: #409eff;
  color: white;
  padding: 8px 5px;
  text-align: center;
  border: 1px solid #ddd;
  font-weight: bold;
}

.pdf-td {
  padding: 6px 5px;
  border: 1px solid #ddd;
  text-align: center;
  color: #333;
}

.pdf-tr:nth-child(even) {
  background-color: #f9f9f9;
}

.pdf-tr:hover {
  background-color: #f5f5f5;
}


:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>