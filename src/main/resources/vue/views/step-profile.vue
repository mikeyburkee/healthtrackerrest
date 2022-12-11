<template id="step-profile">
  <app-layout>
    <div class="card bg-light mb-3" v-if="!noStepFound">
      <div class="card-header">
        <div class="row">
          <div class="col-6"> Step Profile </div>
          <div class="col" align="right">
            <button rel="tooltip" title="Update"
                    class="btn btn-info btn-simple btn-link"
                    @click="updateStep()">
              <i class="far fa-save" aria-hidden="true"></i>
            </button>
            <button rel="tooltip" title="Delete"
                    class="btn btn-info btn-simple btn-link"
                    @click="deleteStep()">
              <i class="fas fa-trash" aria-hidden="true"></i>
            </button>
          </div>
        </div>
      </div>
      <div class="card-body">
        <form>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-step-id">Step ID</span>
            </div>
            <input type="number" class="form-control" v-model="step.id" name="id" readonly placeholder="StepId"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-step-id">User ID</span>
            </div>
            <input type="number" class="form-control" v-model="step.userId" name="userId" readonly placeholder="UserID"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-step-description">Step Count</span>
            </div>
            <input type="number" class="form-control" v-model="step.step_count" name="step_count"  placeholder="StepCount"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-step-date">Date Entered </span>
            </div>
            <input type="text" class="form-control" v-model="step.dateEntry" name="dateEntry"  placeholder="DateEntry"/>
          </div>
        </form>
      </div>
    </div>
  </app-layout>
</template>

<script>
Vue.component("step-profile", {
  template: "#step-profile",
  data: () => ({
    step: null
  }),
  created: function () {
    const stepId = this.$javalin.pathParams["step-id"];
    const url = `/api/steps/${stepId}`
    axios.get(url)
        .then(res => this.step = res.data)
        .catch(() => alert("Error while fetching step" + stepId));
  },
  methods: {
    updateStep: function () {
      const stepId = this.$javalin.pathParams["step-id"];
      const url = `/api/steps/${stepId}`
      axios.patch(url,
          {
            userId: this.step.userId,
            step_count: this.step.step_count,
            dateEntry: this.step.dateEntry
          })
          .then(response =>
              this.step.push(response.data))
          .catch(error => {
            console.log(error)
          })
      alert("Step Entry updated!")
    },
    deleteStep: function () {
      if (confirm("Do you really want to delete?")) {
        const stepId = this.$javalin.pathParams["step-id"];
        const url = `/api/steps/${stepId}`
        axios.delete(url)
            .then(response => {
              alert("Step deleted")
              //display the /users endpoint
              window.location.href = '/steps';
            })
            .catch(function (error) {
              console.log(error)
            });
      }
    }
  }
});
</script>