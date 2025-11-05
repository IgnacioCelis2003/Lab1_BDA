<script lang="ts" setup>

const { data: movies, error, status} = await useFetch('api/movie', {
    lazy: true,
});
</script>

<template>
    <div>
    <article v-if="status === 'pending'" aria-busy="true" />

    <article class="error" v-else-if="error">
       {{  error.statusMessage }} 
    </article>

    <div v-else>
    <article 
    v-for="movie in movies.Search"
    :key="movie.imdbID"
    >
        {{  movie.Title }}
    </article>
    </div>
    </div>
</template>