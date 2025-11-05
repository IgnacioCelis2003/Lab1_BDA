// api key 8fa34f48
type Movie = {
    Title: string;
    Year: string;
    imbdID: string;
    Type: string;
    Poster: string;
}


export default defineEventHandler(async (event): Promise<Movie> => {
    const url = getRequestURL(event);
    return await $fetch(`https://www.omdbapi.com/?apikey=8fa34f48&s=%27Spiderman%27&page=2`)
})