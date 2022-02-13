export function copyOf<T>(input: T) {
    return JSON.parse(JSON.stringify(input)) as T
}
export function getUrl(input: string[]) {
    return input.join('/')
}