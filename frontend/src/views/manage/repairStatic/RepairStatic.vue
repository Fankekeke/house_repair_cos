<template>
  <a-card :bordered="false" class="card-area">
    <div style="background:#ECECEC; padding:30px;margin-top: 30px;margin-bottom: 30px">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <a-row :gutter="15">
          <div>
            <a-col :md="6" :sm="24">
              <a-form-item
                label="选择时间"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-month-picker v-model="queryParams.checkDate" />
              </a-form-item>
            </a-col>
            <!--            <a-col :md="6" :sm="24">-->
            <!--              <a-form-item-->
            <!--                label="房间类型"-->
            <!--                :labelCol="{span: 5}"-->
            <!--                :wrapperCol="{span: 18, offset: 1}">-->
            <!--                <a-select v-model="queryParams.typeId">-->
            <!--                  <a-select-option :value="item.id" v-for="(item, index) in typeList" :key="index">{{ item.typeName }}</a-select-option>-->
            <!--                </a-select>-->
            <!--              </a-form-item>-->
            <!--            </a-col>-->
          </div>
          <span style="float: right; margin-top: 3px;">
            <a-button type="primary" @click="search">查询</a-button>
          </span>
        </a-row>
      </a-form>
    </div>
    <a-row :gutter="20">
      <a-col :span="24">
        <a-card hoverable :bordered="false" style="width: 100%">
          <a-skeleton active v-if="loading" />
          <apexchart v-show="!loading" type="bar" height="300" :options="chartOptions1" :series="series1"></apexchart>
        </a-card>
      </a-col>
      <br/>
<!--      <a-col :span="24">-->
<!--        <a-card hoverable :bordered="false" style="width: 100%">-->
<!--          <a-skeleton active v-if="loading" />-->
<!--          <apexchart v-show="!loading" type="bar" height="300" :options="chartOptions2" :series="series2"></apexchart>-->
<!--        </a-card>-->
<!--      </a-col>-->
    </a-row>
    <a-row :gutter="20">
      <a-col :span="12">
        <a-card hoverable :bordered="false" style="width: 100%">
          <a-skeleton active v-if="loading" />
          <apexchart v-if="!loading" type="donut" height="270" :options="chartOptions3" :series="series3"></apexchart>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card hoverable :bordered="false" style="width: 100%">
          <a-skeleton active v-if="loading" />
          <apexchart v-if="!loading" type="donut" height="270" :options="chartOptions4" :series="series4"></apexchart>
        </a-card>
      </a-col>
    </a-row>
  </a-card>
</template>

<script>
import moment from 'moment'
moment.locale('zh-cn')
export default {
  name: 'Work',
  data () {
    return {
      typeList: [],
      queryParams: {},
      loading: false,
      series1: [],
      chartOptions1: {
        chart: {
          type: 'bar',
          height: 300
        },
        title: {
          text: '维修单统计',
          align: 'left'
        },
        plotOptions: {
          bar: {
            horizontal: false,
            columnWidth: '55%'
          }
        },
        dataLabels: {
          enabled: false
        },
        stroke: {
          show: true,
          width: 2,
          colors: ['transparent']
        },
        xaxis: {
          categories: []
        },
        yaxis: {
          title: {
            text: ''
          }
        },
        fill: {
          opacity: 1
        },
        tooltip: {
          y: {
            formatter: function (val) {
              return val + ' 单'
            }
          }
        }
      },
      series2: [],
      chartOptions2: {
        chart: {
          type: 'bar',
          height: 300
        },
        title: {
          text: '订单收益统计',
          align: 'left'
        },
        plotOptions: {
          bar: {
            horizontal: false,
            columnWidth: '55%'
          }
        },
        dataLabels: {
          enabled: false
        },
        stroke: {
          show: true,
          width: 2,
          colors: ['transparent']
        },
        xaxis: {
          categories: []
        },
        yaxis: {
          title: {
            text: ''
          }
        },
        fill: {
          opacity: 1
        },
        tooltip: {
          y: {
            formatter: function (val) {
              return val + ' 元'
            }
          }
        }
      },
      series3: [],
      chartOptions3: {
        chart: {
          type: 'donut',
          height: 300
        },
        labels: [],
        title: {
          text: '房屋维修统计',
          align: 'left'
        },
        responsive: [{
          breakpoint: 380,
          options: {
            chart: {
              width: 200
            },
            legend: {
              position: 'bottom'
            }
          }
        }]
      },
      series4: [],
      chartOptions4: {
        chart: {
          type: 'donut',
          height: 300
        },
        labels: [],
        title: {
          text: '社区报修统计',
          align: 'left'
        },
        responsive: [{
          breakpoint: 380,
          options: {
            chart: {
              width: 200
            },
            legend: {
              position: 'bottom'
            }
          }
        }]
      }
    }
  },
  mounted () {
    this.search()
  },
  methods: {
    search () {
      this.getStatusList({
        ...this.queryParams
      })
    },
    getStatusList (params = {}) {
      this.loading = true
      if (params.checkDate) {
        params.checkDate = moment(params.checkDate).format('YYYY-MM-DD')
      }
      this.$get(`/cos/repair-info/statistics`, params).then((r) => {
        this.priceByMonth = r.data.priceByMonth
        this.orderNumByMonth = r.data.orderNumByMonth
        this.typeOrderNumRateByMonth = []
        this.typePriceRateByMonth = []

        if (r.data.orderNumByMonth !== null && r.data.orderNumByMonth.length !== 0) {
          let values = []
          if (this.chartOptions1.xaxis.categories.length === 0) {
            this.chartOptions1.xaxis.categories = Array.from(r.data.orderNumByMonth, ({days}) => days)
          }
          let itemData = { name: '房屋维修', data: Array.from(r.data.orderNumByMonth, ({count}) => count) }
          let item1Data = { name: '社区保险哦i', data: Array.from(r.data.fundNumByMonth, ({count}) => count) }
          values.push(itemData)
          values.push(item1Data)
          this.series1 = values
        }

        this.chartOptions3.labels = []
        this.series3 = []
        if (r.data.priceByMonth !== null && r.data.priceByMonth.length !== 0) {
          this.chartOptions3.labels = Array.from(r.data.priceByMonth, ({name}) => name)
          this.series3 = Array.from(r.data.priceByMonth, ({num}) => num)
        }

        this.chartOptions4.labels = []
        this.series4 = []
        if (r.data.fundPriceByMonth !== null && r.data.fundPriceByMonth.length !== 0) {
          this.chartOptions4.labels = Array.from(r.data.fundPriceByMonth, ({name}) => name)
          this.series4 = Array.from(r.data.fundPriceByMonth, ({num}) => num)
        }
        setTimeout(() => {
          this.loading = false
        }, 500)
      })
    }
  }
}
</script>

<style scoped>

</style>
