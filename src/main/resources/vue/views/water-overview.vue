<template id="waters-overview">
  <app-layout>
    <div class="list-group list-group-flush">
      <div class="list-group-item d-flex align-items-start"
           v-for="(water,index) in waters" v-bind:key="index">
        <div class="mr-auto p-2">
          <span><a :href="`/waters/${water.id}`"> Water ID: {{water.id }}  ( Volume: {{ water.volume }} litres, Date: {{ water.dateEntry }})</a></span>
        </div>
        <div class="p2">
          <a :href="`/waters/${water.id}`">
            <button rel="tooltip" title="Update" class="btn btn-info btn-simple btn-link">
              <i class="fa fa-pencil" aria-hidden="true"></i>
            </button>
            <button rel="tooltip" title="Delete" class="btn btn-info btn-simple btn-link"
                    @click="deleteWater(water, index)">
              <i class="fas fa-trash" aria-hidden="true"></i>
            </button>
          </a>
        </div>
      </div>
    </div>
  </app-layout>
</template>
<script>
Vue.component("waters-overview", {
  template: "#waters-overview",
  data: () => ({
    waters: [],
  }),
  created() {
    this.fetchWaters();
  },
  methods: {
    fetchWaters: function () {
      axios.get("/api/waters")
          .then(res => this.waters = res.data)
          .catch(() => alert("Error while fetching waters"));
    }
  }
});
</script>