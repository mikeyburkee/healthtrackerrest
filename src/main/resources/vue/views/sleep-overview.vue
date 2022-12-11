<template id="sleeps-overview">
  <app-layout>
    <div class="list-group list-group-flush">
      <div class="list-group-item d-flex align-items-start"
           v-for="(sleep,index) in sleeps" v-bind:key="index">
        <div class="mr-auto p-2">
          <span><a :href="`/sleeps/${sleep.id}`"> Sleep ID: {{sleep.id }}  ( Duration: {{ sleep.duration }} hours, Date: {{ sleep.wakeUpTime }})</a></span>
        </div>
        <div class="p2">
          <a :href="`/sleeps/${sleep.id}`">
            <button rel="tooltip" title="Update" class="btn btn-info btn-simple btn-link">
              <i class="fa fa-pencil" aria-hidden="true"></i>
            </button>
            <button rel="tooltip" title="Delete" class="btn btn-info btn-simple btn-link"
                    @click="deleteSleep(sleep, index)">
              <i class="fas fa-trash" aria-hidden="true"></i>
            </button>
          </a>
        </div>
      </div>
    </div>
  </app-layout>
</template>
<script>
Vue.component("sleeps-overview", {
  template: "#sleeps-overview",
  data: () => ({
    sleeps: [],
  }),
  created() {
    this.fetchSleeps();
  },
  methods: {
    fetchSleeps: function () {
      axios.get("/api/sleeps")
          .then(res => this.sleeps = res.data)
          .catch(() => alert("Error while fetching sleeps"));
    }
  }
});
</script>