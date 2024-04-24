<template>
  <a-modal v-model="show" title="查看维修信息" @cancel="onClose" :width="800">
    <template slot="footer">
      <a-button key="back" @click="onClose">
        关闭
      </a-button>
      <a-button type="primary" @click="repairDone" v-if="housesData.status == 2">
        维修完成
      </a-button>
    </template>
    <div style="font-size: 13px;font-family: SimHei" v-if="housesData !== null">
      <div style="font-size: 25px;color: #000c17;text-align: center">
        <p v-if="housesData.status == 0">请稍后，正在为您指定维修人员</p>
        <p v-if="housesData.status == 2">维修人员正在维修中！</p>
      </div>
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
      workerList: ''
    }
  },
  methods: {
    repairDone () {
      this.housesData.status = 4
      this.$put('/cos/community-repair-info/down', this.housesData).then((r) => {
        this.$emit('success')
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
    handleSubmit () {
      let data = Object.assign(this.housesData, {})
      if (this.workerId !== null) {
        data.worker = this.workerId
        this.$put('/cos/repair-info', data).then((r) => {
          this.workerId = null
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
