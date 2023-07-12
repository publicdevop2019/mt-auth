export class Result {
    errorMsg?: string
    public static success() {
        const next = new Result()
        return next
    }
    public static failed(message: string) {
        const next = new Result()
        next.errorMsg = message
        return next
    }
}
export class Validator {
    public static isString(value: string): Result {
        if (typeof value !== 'string') {
            return Result.failed('STRING_TYPE_MATCH')
        } else {
            return Result.success()
        }
    }
    public static notEmpty(value: string) {
        if (value === '') {
            return Result.failed('STRING_NOT_EMPTY_STRING')
        } else {
            return Result.success()
        }
    }
    public static notBlank(value: string) {
        if (value.trim() === '') {
            return Result.failed('STRING_NOT_BLAND_STRING')
        } else {
            return Result.success()
        }
    }
    public static exist(var0: any) {
        if (var0 === null || var0 === undefined || var0 === '' || var0 === 0) {
            return Result.failed('DEFAULT_NOT_NULL')
        } else {
            return Result.success()
        }
    }
    public static same(var0: any, var1: any) {
        if (var0 !== var1) {
            return Result.failed('DEFAULT_NOT_SAME')
        } else {
            return Result.success()
        }
    }
}