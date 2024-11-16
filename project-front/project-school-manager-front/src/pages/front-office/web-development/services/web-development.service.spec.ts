import { TestBed } from '@angular/core/testing';

import { WebDevelopmentService } from './web-development.service';

describe('WebDevelopmentService', () => {
  let service: WebDevelopmentService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WebDevelopmentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
