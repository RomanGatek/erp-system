import { AxiosError } from "axios";

export enum ErrorType {
    SERVER = "SERVER",
    CLIENT = "CLIENT",
    UNKNOWN = "UNKNOWN",
}

export type RawError = AxiosError | Error;

/**
 * Get error type from givven error
 * @param {RawError} error
 * @returns {ErrorType}
 */
export const getErrorType = (error: RawError): ErrorType => {
    if (error instanceof AxiosError) {
        return ErrorType.SERVER;
    } else if (error instanceof Error) {
        return ErrorType.CLIENT;
    } else {
        return ErrorType.UNKNOWN;
    }
};


export type ErrorItem = {
    field?: string;
    message?: string
}

export type ValidationResponse = {
    errors: ErrorItem[],
    code?: number,
    general?: string
}

export type DefaultResponse = {
    cause?: string,
    message: string
}

export type ReponseErrorEntity = DefaultResponse | ValidationResponse


export const errorParser = (error: RawError): ReponseErrorEntity => {
    const errorType = getErrorType(error);
    let response: ReponseErrorEntity = { errors: [] } as ValidationResponse

    switch (errorType) {
        case ErrorType.CLIENT: {
            const rsp = {} as DefaultResponse
            rsp.cause = error.stack + "",
            rsp.message = error.message
            response = rsp
            break;
        }
        case ErrorType.SERVER: {
            const axiosError: AxiosError = error as AxiosError;
            const rsp_ = {errors:[]} as ValidationResponse
            if (Array.isArray(axiosError.response?.data)) {
                const rsp = axiosError.response!!.data as ErrorItem[];
                const arrayProcess = (
                    errorArray: ErrorItem[]
                ) => {
                    errorArray.forEach((e) => {
                        const { field, message } = e;
                        if (message?.startsWith("[") && message.includes(";")) {
                            const parts: string[] = message.split(";");
                            const field = parts[0]?.slice(1, message.indexOf(";") - 1);
                            const errorMessage = parts[1]?.slice(message.indexOf(";") - 1);
                            rsp_['errors'].push({ field, message: errorMessage })
                        } else {
                            rsp_['errors'].push({ field, message })
                        }
                    });
                };
                arrayProcess(rsp)
            } else {
                const { field, message } = axiosError.response?.data as ErrorItem;
                if (message?.startsWith("[") && message.includes(";")) {
                    const parts: string[] = message.split(";");
                    const field = parts[0]?.slice(1, message.indexOf(";") - 1);
                    const errorMessage = parts[1]?.slice(message.indexOf(";") - 1);
                    rsp_['errors'].push({ field, message: errorMessage })
                } else {
                    rsp_['errors'].push({ field, message })
                }
            }
            rsp_.code = axiosError.response!!.status;
            response = rsp_
            break;
        }
    }

    return response;
};