export interface CheckoutRequest {
  planId: number;
  successUrl: string;
  cancelUrl: string;
}

export interface CheckoutResponse {
  sessionId: string;
  checkoutUrl: string;
}
