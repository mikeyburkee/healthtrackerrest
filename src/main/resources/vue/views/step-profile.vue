<template id="step-profile">
  <app-layout>
    <div>
      <form v-if="step">
        <label class="col-form-label">Step ID: </label>
        <input class="form-control" v-model="step.id" name="id" type="number" readonly/><br>
        <label class="col-form-label">User Id: </label>
        <input class="form-control" v-model="step.userId" name="userId" type="number" readonly/><br>
        <label class="col-form-label">Step Count: </label>
        <input class="form-control" v-model="step.step_count" name="step-count" type="number"/><br>
        <label class="col-form-label">Date: </label>
        <input class="form-control" v-model="step.dateEntry" name="dateEntry" type="text"/><br>
      </form>
      <dt v-if="step">
        <br>
        <a :href="`/users/${step.userId}`">View User Profile</a>
      </dt>
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
  }
});
</script>