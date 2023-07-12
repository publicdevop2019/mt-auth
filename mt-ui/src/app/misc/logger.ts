import { environment } from "src/environments/environment"

export class Logger {
    private static LOG_LEVEL: 'DEBUG' | 'TRACE' | 'ERROR' = environment.production ? 'ERROR' : 'DEBUG'
    public static debug(message: any, ...args: any[]) {
        if (Logger.LOG_LEVEL === 'DEBUG') {
            this.log(message, args)
        }
    }
    public static trace(message: any, ...args: any[]) {
        if (Logger.LOG_LEVEL === 'TRACE') {
            this.log(message, args)
        }
    }
    public static error(message: any, ...args: any[]) {
        if (Logger.LOG_LEVEL === 'ERROR') {
            this.log(message, args)
        }
    }
    public static log(message: string, args: any[]) {
        args.forEach(e => {
            if (typeof e !== 'string' || typeof e !== 'boolean') {
                message = message.replace('{}', JSON.stringify(e))
            } else {
                message = message.replace('{}', e)
            }
        })
        console.log(message)
    }
    public static debugObj(message: any, ...args: any[]) {
        if (Logger.LOG_LEVEL === 'DEBUG') {
            this.logObj(message, args)
        }
    }
    public static traceObj(message: any, ...args: any[]) {
        if (Logger.LOG_LEVEL === 'TRACE') {
            this.logObj(message, args)
        }
    }
    public static errorObj(message: any, ...args: any[]) {
        if (Logger.LOG_LEVEL === 'ERROR') {
            this.logObj(message, args)
        }
    }
    public static logObj(message: string, args: any[]) {
        console.log(message)
        args.forEach(arg => {
            console.dir(arg)
        })
    }
}