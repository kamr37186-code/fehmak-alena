import { Component } from '@angular/core';

import { SubscriptionService } from '../../core/services/subscription.service';

@Component({
  selector: 'app-pricing',
  standalone: true,
  imports: [],
  templateUrl: './pricing.component.html'
})
export class PricingComponent {
  isProcessing = false;
  
  constructor(private subscriptionService: SubscriptionService) {}

  subscribe(planId: number) {
    this.isProcessing = true;
    const request = {
      planId: planId,
      successUrl: window.location.origin + '/pricing/success',
      cancelUrl: window.location.origin + '/pricing/cancel'
    };
    
    this.subscriptionService.createCheckoutSession(request).subscribe({
      next: (response) => {
        window.location.href = response.checkoutUrl;
      },
      error: (err) => {
        console.error('Error creating checkout session', err);
        this.isProcessing = false;
      }
    });
  }
}
