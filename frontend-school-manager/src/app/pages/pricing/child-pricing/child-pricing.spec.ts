import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChildPricing } from './child-pricing';

describe('ChildPricing', () => {
  let component: ChildPricing;
  let fixture: ComponentFixture<ChildPricing>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChildPricing]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChildPricing);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
