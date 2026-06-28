import { Component } from '@angular/core';

@Component({
  selector: 'app-payment-success',
  standalone: true,
  template: `
    <div class="min-h-screen bg-gray-50 flex items-center justify-center p-8">
      <div class="bg-white p-10 rounded-3xl shadow-xl max-w-md text-center border border-gray-200">
        <div class="w-24 h-24 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-8 text-green-500">
          <svg class="w-12 h-12" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>
        </div>
        <h2 class="text-3xl font-extrabold text-gray-900 mb-4 tracking-tight">Payment Successful!</h2>
        <p class="text-lg text-gray-600 mb-10 leading-relaxed">Thank you for subscribing to Fahmak Alena. Your account has been successfully upgraded.</p>
        <a href="/dashboard" class="inline-block w-full px-8 py-4 bg-brand-600 text-white font-bold rounded-xl hover:bg-brand-700 shadow-md transition-all">Go to Dashboard</a>
      </div>
    </div>
  `
})
export class PaymentSuccessComponent {}
