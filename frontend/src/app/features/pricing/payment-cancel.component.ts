import { Component } from '@angular/core';

@Component({
  selector: 'app-payment-cancel',
  standalone: true,
  template: `
    <div class="min-h-screen bg-gray-50 flex items-center justify-center p-8">
      <div class="bg-white p-10 rounded-3xl shadow-xl max-w-md text-center border border-gray-200">
        <div class="w-24 h-24 bg-yellow-100 rounded-full flex items-center justify-center mx-auto mb-8 text-yellow-500">
          <svg class="w-12 h-12" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.962-.833-2.732 0L4.082 16.5c-.77.833.192 2.5 1.732 2.5z"></path></svg>
        </div>
        <h2 class="text-3xl font-extrabold text-gray-900 mb-4 tracking-tight">Payment Canceled</h2>
        <p class="text-lg text-gray-600 mb-10 leading-relaxed">Your checkout process was canceled. No charges were made to your account.</p>
        <a href="/pricing" class="inline-block w-full px-8 py-4 bg-white border-2 border-gray-200 text-gray-800 font-bold rounded-xl hover:bg-gray-50 transition-all">Return to Pricing</a>
      </div>
    </div>
  `
})
export class PaymentCancelComponent {}
