export function copyOf<T>(input:T){
    return JSON.parse(JSON.stringify(input)) as T
}