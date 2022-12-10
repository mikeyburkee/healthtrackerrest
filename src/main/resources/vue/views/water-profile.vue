<template id="water-profile">
  <app-layout>
    <div>
      <form v-if="water">
        <label class="col-form-label">Water ID: </label>
        <input class="form-control" v-model="water.id" name="id" type="number" readonly/><br>
        <label class="col-form-label">User Id: </label>
        <input class="form-control" v-model="water.userId" name="userId" type="number" readonly/><br>
        <label class="col-form-label">Volume (litres): </label>
        <input class="form-control" v-model="water.volume" name="volume" type="number"/><br>
        <label class="col-form-label">Date: </label>
        <input class="form-control" v-model="water.dateEntry" name="dateEntry" type="text"/><br>
      </form>
      <dt v-if="water">
        <br>
        <a :href="`/users/${water.userId}`">View User Profile</a>
      </dt>
    </div>
  </app-layout>
</template>

<script>
Vue.component("water-profile", {
  template: "#water-profile",
  data: () => ({
    water: null
  }),
  created: function () {
    const waterId = this.$javalin.pathParams["water-id"];
    const url = `/api/waters/${waterId}`
    axios.get(url)
        .then(res => this.water = res.data)
        .catch(() => alert("Error while fetching water" + waterId));
  }
});
</script>