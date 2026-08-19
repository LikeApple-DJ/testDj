<template>
  <div class="dept-tree-container">
    <div class="tree-header">
      <h3>部门架构</h3>
      <el-button type="primary" size="small" @click="refreshTree">
        <el-icon><Refresh /></el-icon>
      </el-button>
    </div>
    <el-tree
      :data="treeData"
      :props="{ label: 'name', children: 'children' }"
      node-key="id"
      :highlight-current="true"
      :expand-on-click-node="false"
      :default-expanded-keys="expandedKeys"
      draggable
      @node-click="handleNodeClick"
      @node-expand="handleNodeExpand"
      @node-collapse="handleNodeCollapse"
      @node-drop="handleNodeDrop"
    >
      <template #default="{ node, data }">
        <span class="tree-node">
          <el-icon><OfficeBuilding /></el-icon>
          <span class="node-label">{{ data.name }}</span>
        </span>
      </template>
    </el-tree>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDepartmentTree, moveDepartment } from '../api/department'

const emit = defineEmits(['select', 'move'])

const treeData = ref([])
const expandedKeys = ref([])

onMounted(() => {
  refreshTree()
})

const refreshTree = async () => {
  try {
    const res = await getDepartmentTree()
    if (res.code === 200) {
      treeData.value = res.data || []
      // 默认展开第一级
      if (treeData.value.length > 0 && expandedKeys.value.length === 0) {
        expandedKeys.value = treeData.value.map(item => item.id)
      }
    }
  } catch (error) {
    ElMessage.error('加载部门树失败')
  }
}

const handleNodeClick = (data) => {
  emit('select', data.id)
}

const handleNodeExpand = (data) => {
  if (!expandedKeys.value.includes(data.id)) {
    expandedKeys.value.push(data.id)
  }
}

const handleNodeCollapse = (data) => {
  const index = expandedKeys.value.indexOf(data.id)
  if (index > -1) {
    expandedKeys.value.splice(index, 1)
  }
}

const handleNodeDrop = async (draggingNode, dropNode, dropType) => {
  const id = draggingNode.data.id
  let newParentId = null

  if (dropType === 'inner') {
    newParentId = dropNode.data.id
  } else {
    newParentId = dropNode.data.parentId
  }

  try {
    const res = await moveDepartment(id, newParentId)
    if (res.code === 200) {
      ElMessage.success('部门移动成功')
      emit('move')
      await refreshTree()
    }
  } catch (error) {
    ElMessage.error(error.message || '部门移动失败')
    // 还原树到拖拽前状态
    await nextTick()
    refreshTree()
  }
}

defineExpose({ refreshTree })
</script>

<style scoped>
.dept-tree-container {
  padding: 10px;
  height: 100%;
}

.tree-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.tree-header h3 {
  margin: 0;
  font-size: 16px;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 5px;
}

.node-label {
  font-size: 14px;
}
</style>
