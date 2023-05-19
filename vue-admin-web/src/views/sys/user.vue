<template>
  <div>
    <el-card id="search">
      <el-row>
        <el-col :span="18">
          <el-input v-model="searchModel.username" placeholder="用户名" clearable />
          <el-input v-model="searchModel.phone" placeholder="电话" clearable />
          <el-button type="primary" round icon="el-icon-search" @click="getUserList">查询</el-button>
        </el-col>
        <el-col :span="6" align="right">
          <el-button icon="el-icon-download" type="primary" @click="downdate">保存为excel</el-button>
          <el-button type="primary" icon="el-icon-plus" circle @click="openEditUI(null)" />
        </el-col>
      </el-row>
    </el-card>
    <!-- 查询结果 -->
    <el-table
      :data="userList"
      stripe
      style="width: 100%"
    >
      <!-- (pageNo - 1) * pageSoize + index + 1 -->
      <el-table-column
        label="#"
        width="100"
      >
        <template slot-scope="scope">
          {{ (searchModel.pageNo -1) * searchModel.pageSize + scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column
        prop="id"
        label="用户ID"
        width="180"
      />
      <el-table-column
        prop="username"
        label="用户名"
        width="180"
      />
      <el-table-column
        prop="phone"
        label="电话"
        width="180"
      />
      <el-table-column
        prop="status"
        label="用户状态"
      >
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status == 1">正常</el-tag>
          <el-tag v-if="scope.row.status == 0" type="danger">禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="email"
        label="电子邮箱"
      />
      <el-table-column
        label="操作"
        width="200"
      >
        <template slot-scope="scope">
          <el-button type="primary" icon="el-icon-edit" circle size="mini" @click="openEditUI(scope.row.id)" />
          <el-button type="danger" icon="el-icon-delete" circle size="mini" @click="deleteUser(scope.row)" />
        </template>
      </el-table-column>
    </el-table>
    <el-card />
    <!-- 分页 -->
    <el-pagination
      :current-page="searchModel.pageNo"
      :page-sizes="[5, 10, 20, 50]"
      :page-size="searchModel.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />

    <!-- 用户信息编辑对话框 -->
    <el-dialog :title="title" :visible.sync="dialogFormVisible" @close="clearForm">
      <el-form ref="userFormRef" :model="userForm" :rules="rules">
        <el-form-item label="用户名" :label-width="formLabelWidth" prop="username">
          <el-input v-model="userForm.username" autocomplete="off" />
        </el-form-item>
        <el-form-item v-if="userForm.id == null || userForm.id == undefined" label="密码" :label-width="formLabelWidth" prop="password">
          <el-input v-model="userForm.password" autocomplete="off" type="password" />
        </el-form-item>
        <el-form-item label="联系电话" :label-width="formLabelWidth" prop="phone">
          <el-input v-model="userForm.phone" autocomplete="off" />
        </el-form-item>
        <el-form-item label="用户状态" :label-width="formLabelWidth">
          <el-switch v-model="userForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="用户角色" :label-width="formLabelWidth">
          <el-checkbox-group v-model="userForm.roleIdList" style="width: 85%;" :max="2">
            <el-checkbox v-for="(item) in roleList" :key="item.roleId" :label="item.roleId">
              {{ item.roleDesc }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="电子邮箱" :label-width="formLabelWidth" prop="email">
          <el-input v-model="userForm.email" autocomplete="off" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveUser">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import userApi from '@/api/userMange'
import roleApi from '@/api/roleMange'
export default {
  data: function() {
    var checkEmail = (rule, value, callback) => {
      const mailReg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/
      if (!value) {
        return callback(new Error('邮箱不能为空'))
      }
      setTimeout(() => {
        if (mailReg.test(value)) {
          callback()
        } else {
          callback(new Error('请输入正确的邮箱格式'))
        }
      }, 100)
    }
    return {
      roleList: [],
      formLabelWidth: '130px',
      userForm: {
        roleIdList: []
      },
      dialogFormVisible: false,
      title: '',
      total: 0,
      searchModel: {
        pageNo: 1,
        pageSize: 10
      },
      userList: [],
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, trigger: 'blur', validator: checkEmail }
        ],
        phone: [
          { required: true, message: '手机号必填', trigger: 'blur' },
          { pattern: /^1[3456789]\d{9}$/, message: '手机号码格式不正确', trigger: 'blur' }
        ]
      }

    }
  },
  created() {
    this.getUserList()
    this.getAllRole()
  },
  methods: {
    getAllRole() {
      roleApi.getAllRole().then(res => {
        this.roleList = res.data
      })
    },
    deleteUser(user) {
      this.$confirm(`确认删除用户${user.username}`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        userApi.deleteUserById(user.id).then(res => {
          this.$message({
            type: 'success',
            message: res.message
          })
          this.getUserList()
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    clearForm() {
      this.userForm = {
        roleIdList: []
      }
      this.$refs.userFormRef.clearValidate()
    },
    openEditUI(id) {
      if (id == null) {
        this.title = '新增用户'
      } else {
        this.title = '修改用户'
        userApi.getUserById(id).then(res => {
          this.userForm = res.data
        })
      }

      this.dialogFormVisible = true
    },
    handleSizeChange(pageSize) {
      this.searchModel.pageSize = pageSize
      this.getUserList()
    },
    handleCurrentChange(pageNo) {
      this.searchModel.pageNo = pageNo
      this.getUserList()
    },
    getUserList() {
      userApi.getUserList(this.searchModel).then(response => {
        this.userList = response.data.rows
        this.total = response.data.total
      })
    },
    saveUser() {
      this.$refs.userFormRef.validate((valid) => {
        if (valid) {
          userApi.saveUser(this.userForm).then(response => {
            this.$message({
              message: response.message,
              type: 'success'
            })
            this.dialogFormVisible = false
            this.getUserList()
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    formatJson(headValue, jsonData) {
      return jsonData.map((v) => headValue.map((j) => v[j]))
    },
    downdate() {
      const theader = ['username', 'phone', 'status', 'email']
      const list = this.userList
      const tdata = this.formatJson(theader, list)
      import('@/vendor/Export2Excel').then(excel => {
        excel.export_json_to_excel({
          header: theader, // 表头 必填
          data: tdata, // 具体数据 必填
          filename: '维新派', // 非必填
          autoWidth: true, // 非必填
          bookType: 'xlsx'
        })
      })
    }

  }
}
</script>

<style>
#search .el-input {
  width: 200px;
  margin-right:15px;
}
.el-dialog .el-input{
  width: 70%;
}
</style>
