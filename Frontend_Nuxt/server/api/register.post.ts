export default defineEventHandler(async (event) => {

    const data = await readBody(event);

    if(!(data.email && data.password && data.nombre)){
        throw createError("Se deben de pasar los datos del usuario en DataBody");
    }

    return await $fetch(`api/auth/register`, {
        method: "POST",
        body: {
            nombre: data.nombre,
            email: data.email,
            password: data.password
        }
    })

})