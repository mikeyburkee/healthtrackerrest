<template id="user-overview">
  <app-layout>

    <!-- ************** TOGGLE BAR TO OPEN UP ADD USER FORM *************   -->
    <div class="card bg-light mb-3">
      <div class="card-header">
        <div class="row">
          <div class="col-6">
            Users
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

    <!-- ************** SECTION ADD USER FORM *************   -->
    <div class="card-body" :class="{ 'd-none': hideForm}">
      <form id="createUser">
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="input-user-name">Name</span>
          </div>
          <input type="text" class="form-control" v-model="formData.name" name="name" placeholder="Name"/>
        </div>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="input-user-email">Email</span>
          </div>
          <input type="email" class="form-control" v-model="formData.email" name="email" placeholder="Email"/>
        </div>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="input-user-height">Height</span>
          </div>
          <input type="number" class="form-control" v-model="formData.height" name="height" placeholder="Height"/>
        </div>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="input-user-weight">Weight</span>
          </div>
          <input type="number" class="form-control" v-model="formData.weight" name="weight" placeholder="Weight"/>
        </div>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="input-user-age">Age</span>
          </div>
          <input type="number" class="form-control" v-model="formData.age" name="age" placeholder="Age"/>
        </div>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="input-user-gender">Gender</span>
          </div>
          <input type="text" class="form-control" v-model="formData.gender" name="gender" placeholder="Gender"/>
        </div>
      </form>
      <button rel="tooltip" title="createUser" class="btn btn-info btn-simple btn-link"
              @click= "addUser()">
        <i class="far fa-save" aria-hidden="true"></i>
      </button>
    </div>

    <!-- ************** SECTION TO LIST ALL USER *************   -->
    <div class="list-group list-group-flush">
      <div class="list-group-item d-flex align-items-start"
           v-for="(user,index) in users" v-bind:key="index">
        <div class="mr-auto p-2">
          <span><a :href="`/users/${user.id}`"> {{ user.name }} ({{ user.email }})</a></span>
        </div>
        <div class="p2">
          <a :href="`/users/${user.id}`">
            <button rel="tooltip" title="Update" class="btn btn-info btn-simple btn-link">
              <i class="fa fa-pencil" aria-hidden="true"></i>
            </button>
            <button rel="tooltip" title="Delete" class="btn btn-info btn-simple btn-link"
                    @click="deleteUser(user, index)">
              <i class="fas fa-trash" aria-hidden="true"></i>
            </button>
          </a>
        </div>
      </div>
    </div>
  </app-layout>
</template>

<script>
Vue.component("user-overview", {
  template: "#user-overview",
  data: () => ({
    users: [],
    formData: [],
    hideForm : true,
  }),
  created() {
    this.fetchUsers();
  },
  methods: {
    fetchUsers: function () {
      axios.get("/api/users")
          .then(res => this.users = res.data)
          .catch(() => alert("Error while fetching users"));
    },
    deleteUser: function (user, index) {
      if (confirm('Are you sure you want to delete this user? This action cannot be undone.', 'Warning')) {
        //user confirmed delete
        const userId = user.id;
        const url = `/api/users/${userId}`;
        axios.delete(url)
            .then(response => {
              alert("User deleted")
              //display the /users endpoint
              window.location.href = '/users';
            })
            .catch(function (error) {
              console.log(error)
            });
      }

    },
    addUser: function (){
      const url = `/api/users`;
      axios.post(url,
          {
            name: this.formData.name,
            email: this.formData.email,
            weight: this.formData.weight,
            height: this.formData.height,
            age: this.formData.age,
            gender: this.formData.gender
          })
          .then(response => {
            this.users.push(response.data)
            this.hideForm= true;
          })
          .catch(error => {
            console.log(error)
          })
    }
  }
});
</script>