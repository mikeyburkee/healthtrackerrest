<template id="steps-overview">
  <app-layout>
    <!-- ************** TOGGLE BAR TO OPEN UP ADD STEP FORM *************   -->
    <div class="card bg-light mb-3">
      <div class="card-header">
        <div class="row">
          <div class="col-6">
            Steps
          </div>
          <div class="col" align="right">
            <button rel="tooltip" title="Add"
                    class="btn btn-info btn-simple btn-link"
                    @click="hideForm =!hideForm">
              <i class="fa fa-plus" aria-hidden="true"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- ************** SECTION ADD STEP FORM *************   -->
    <div class="card-body" :class="{ 'd-none': hideForm}">
      <form id="addStep">
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="input-step-userId">User Id</span>
          </div>
          <input type="number" class="form-control" v-model="formData.userId" name="userId" placeholder="UserId"/>
        </div>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="input-step-stepCount">Step Count</span>
          </div>
          <input type="number" class="form-control" v-model="formData.step_count" name="step_count" placeholder="Step_count"/>
        </div>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="input-user-height">Date</span>
          </div>
          <input type="text" class="form-control" v-model="formData.dateEntry" name="dateEntry" placeholder="DateEntry"/>
        </div>
      </form>
      <button rel="tooltip" title="addStep" class="btn btn-info btn-simple btn-link"
              @click= "addStep()">
        <i class="far fa-save" aria-hidden="true"></i>
      </button>
    </div>

    <!-- ************** SECTION TO LIST ALL STEPS *************   -->
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
    formData: [],
    hideForm : true,
  }),
  created() {
    this.fetchSteps();
  },
  methods: {
    fetchSteps: function () {
      axios.get("/api/steps")
          .then(res => this.steps = res.data)
          .catch(() => alert("Error while fetching steps"));
    },
    deleteStep: function (step, index) {
      if (confirm('Are you sure you want to delete this step entry? This action cannot be undone.', 'Warning')) {
        //user confirmed delete
        const stepId = step.id;
        const url = `/api/steps/${stepId}`;
        axios.delete(url)
            .then(response => {
              alert("Step entry deleted")
              //display the /steps endpoint
              window.location.href = '/steps';
            })
            .catch(function (error) {
              console.log(error)
            });
      }
    },
    addStep: function (){
      const url = `/api/steps`;
      axios.post(url,
          {
            userId: this.formData.userId,
            step_count: this.formData.step_count,
            dateEntry: this.formData.dateEntry
          })
          .then(response => {
            this.steps.push(response.data)
            this.hideForm= true;
          })
          .catch(error => {
            console.log(error)
          })
    }
  },
});
</script>