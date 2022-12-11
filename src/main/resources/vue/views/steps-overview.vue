<template id="steps-overview">
  <app-layout>
    <div class="list-group list-group-flush">
      <div class="list-group-item d-flex align-items-start"
           v-for="(step,index) in steps" v-bind:key="index">
        <div class="mr-auto p-2">
          <span><a :href="`/steps/${step.id}`"> Steps ID: {{step.id }} ( Step count: {{ step.step_count }}, Date: {{ step.dateEntry }})</a></span>
        </div>
        <div class="p2">
          <a :href="`/steps/${step.id}`">
            <button rel="tooltip" title="Update" class="btn btn-info btn-simple btn-link">
              <i class="fa fa-pencil" aria-hidden="true"></i>
            </button>
            <button rel="tooltip" title="Delete" class="btn btn-info btn-simple btn-link"
                    @click="deleteStep(step, index)">
              <i class="fas fa-trash" aria-hidden="true"></i>
            </button>
          </a>
        </div>
      </div>
    </div>
  </app-layout>
</template>

<script>
Vue.component("steps-overview", {
  template: "#steps-overview",
  data: () => ({
    steps: [],
  }),
  created() {
    this.fetchSteps();
  },
  methods: {
    fetchSteps: function () {
      axios.get("/api/steps")
          .then(res => this.steps = res.data)
          .catch(() => alert("Error while fetching steps"));
    }
  }
});
</script>