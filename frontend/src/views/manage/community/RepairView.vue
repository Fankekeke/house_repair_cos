<template>
  <a-modal v-model="show" title="查看维修信息" @cancel="onClose" :width="800">
    <template slot="footer">
      <a-button type="primary" key="back1" @click="handleSubmitStatus(2)" v-if="currentUser.roleId == 76 && housesData.status == 1">
        通过
      </a-button>
      <a-button type="danger" key="back2" @click="handleSubmitStatus(3)" v-if="currentUser.roleId == 76 && housesData.status == 1">
        驳回
      </a-button>
      <a-button key="back3" @click="onClose">
        关闭
      </a-button>
    </template>
    <div style="font-size: 13px;font-family: SimHei" v-if="housesData !== null">
<!--      <div style="font-size: 25px;color: #000c17;text-align: center">-->
<!--        <p v-if="housesData.status == 0">请稍后，正在为您指定维修人员</p>-->
<!--        <p v-if="housesData.status == 1">维修人员正在维修中！</p>-->
<!--      </div>-->
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">基础信息</span></a-col>
        <a-col :span="8"><b>业主姓名：</b>
          {{ housesData.ownerName }}
        </a-col>
        <a-col :span="8"><b>联系电话：</b>
          {{ housesData.phone }}
        </a-col>
        <a-col :span="8"><b>创建时间：</b>
          {{ housesData.createDate !== null ? housesData.createDate : '- -' }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="8"><b>维修编号：</b>
          {{ housesData.code }}
        </a-col>
        <a-col :span="16"><b>维修内容：</b>
          {{ housesData.content }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="8"><b>详细地址：</b>
          {{ housesData.address }}
        </a-col>
        <a-col :span="8"><b>价格：</b>
          {{ housesData.price !== null ? housesData.price : '- -' }}
        </a-col>
        <a-col :span="8"><b>完成时间：</b>
          {{ housesData.overDate !== null ? housesData.overDate : '- -' }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="24"><b>支出明细：</b>
          {{ housesData.remark !== null ? housesData.remark : '- -' }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="24">
          <a-upload
            name="avatar"
            action="http://127.0.0.1:9527/file/fileUpload/"
            list-type="picture-card"
            disabled
            :file-list="fileList"
            @preview="handlePreview"
            @change="picHandleChange"
          >
          </a-upload>
          <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">
            <img alt="example" style="width: 100%" :src="previewImage" />
          </a-modal>
        </a-col>
      </a-row>
      <br/>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;" v-if="housesData.staffId !== null">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">维修员工信息</span></a-col>
        <a-col :span="8"><b>员工姓名：</b>
          <a-popover>
            <template slot="content">
              <a-avatar v-if="housesData.staffImages !== null" shape="square" :size="132" icon="user" :src="'http://127.0.0.1:9527/imagesWeb/' + housesData.staffImages.split(',')[0]" />
              <a-avatar v-else shape="square" :size="132" icon="user" />
            </template>
            <a>{{ housesData.staffName !== null ? housesData.staffName : '- -' }}</a>
          </a-popover>
        </a-col>
        <a-col :span="8"><b>联系电话：</b>
          {{ housesData.staffPhone }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;" :gutter="15" v-if="housesData.status == 0 || housesData.status == 3">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">基金余额</span></a-col>
        <a-col :span="8">
          {{ max }} 元
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;" :gutter="15" v-if="housesData.status == 0 || housesData.status == 3">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">维修费用</span></a-col>
        <a-col :span="13">
          <a-input-number style="width: 100%" :min="0" :max="max" v-model="price"/>
        </a-col>
        <a-col :span="24" style="margin-top: 15px">
          <div style="font-size: 15px;font-weight: 650;color: #000c17">支出明细</div>
          <a-textarea :auto-size="{ minRows: 5, maxRows: 6 }" v-model="remark"/>
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;" :gutter="15" v-if="housesData.status == 0 || housesData.status == 3">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">更换维修员工</span></a-col>
        <a-col :span="13">
          <a-select v-model="workerId" style="width: 100%">
            <a-select-option v-for="(item, index) in workerList" :value="item.id" :key="index">{{ item.name }}</a-select-option>
          </a-select>
        </a-col>
        <a-col :span="3">
          <a-button type="primary" key="back" @click="handleSubmit">
            提交
          </a-button>
        </a-col>
      </a-row>
      <br/>
    </div>
  </a-modal>
</template>

<script>
import {mapState} from 'vuex'
const formItemLayout = {
  labelCol: { span: 24 },
  wrapperCol: { span: 24 }
}
function getBase64 (file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = error => reject(error)
  })
}
export default {
  name: 'RepairEdit',
  props: {
    repairEditVisiable: {
      default: false
    },
    housesData: {
      type: Object
    }
  },
  watch: {
    repairEditVisiable: function (value) {
      if (value && this.housesData.images) {
        this.imagesInit(this.housesData.images)
      }
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    show: {
      get: function () {
        return this.repairEditVisiable
      },
      set: function () {
      }
    }
  },
  mounted () {
    this.getWorkerList()
    this.getRepairFund()
  },
  data () {
    return {
      rowId: null,
      formItemLayout,
      form: this.$form.createForm(this),
      loading: false,
      fileList: [],
      previewVisible: false,
      previewImage: '',
      workerId: null,
      workerList: '',
      remark: '',
      max: 0,
      price: 0
    }
  },
  methods: {
    repairDone () {
      this.$get('/cos/repair-info/down', { repairId: this.housesData.id }).then((r) => {
        this.$emit('success')
      })
    },
    getRepairFund () {
      this.$get('/cos/repair-record-info/fund').then((r) => {
        this.max = r.data.data.price
      })
    },
    getWorkerList () {
      this.$get('/cos/worker-info/list', { type: 2 }).then((r) => {
        this.workerList = r.data.data
      })
    },
    handleCancel () {
      this.previewVisible = false
    },
    async handlePreview (file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj)
      }
      this.previewImage = file.url || file.preview
      this.previewVisible = true
    },
    picHandleChange ({ fileList }) {
      this.fileList = fileList
    },
    imagesInit (images) {
      if (images !== null && images !== '') {
        let imageList = []
        images.split(',').forEach((image, index) => {
          imageList.push({uid: index, name: image, status: 'done', url: 'http://127.0.0.1:9527/imagesWeb/' + image})
        })
        this.fileList = imageList
      }
    },
    reset () {
      this.loading = false
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    handleSubmitStatus (status) {
      let data = Object.assign(this.housesData, {})
      data.status = status
      this.$put('/cos/community-repair-info', data).then((r) => {
        this.workerId = null
        this.remark = ''
        this.$emit('success')
      }).catch(() => {
        this.loading = false
      })
    },
    handleSubmit () {
      let data = Object.assign(this.housesData, {})
      if (this.workerId !== null) {
        data.staffId = this.workerId
        data.price = this.price
        data.remark = this.remark
        data.status = 1
        this.$put('/cos/community-repair-info', data).then((r) => {
          this.workerId = null
          this.remark = ''
          this.$emit('success')
        }).catch(() => {
          this.loading = false
        })
      } else {
        this.$message.warning('请选择工作人员！')
      }
    }
  }
}
</script>

<style scoped>

</style>
