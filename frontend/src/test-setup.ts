import '@angular/compiler';
import '@analogjs/vite-plugin-angular/setup-vitest';
import { getTestBed } from '@angular/core/testing';
import {
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting,
} from '@angular/platform-browser-dynamic/testing';
import { expect } from 'vitest';

try {
  getTestBed().initTestEnvironment(
    BrowserDynamicTestingModule,
    platformBrowserDynamicTesting()
  );
} catch (e) {
  // ignore
}

expect.extend({
  toBeTrue(received) {
    return {
      pass: received === true,
      message: () => `expected ${received} to be true`,
    };
  },
  toBeFalse(received) {
    return {
      pass: received === false,
      message: () => `expected ${received} to be false`,
    };
  }
});

if (typeof window !== 'undefined') {
  Object.defineProperty(window, 'matchMedia', {
    writable: true,
    value: vi.fn().mockImplementation(query => ({
      matches: false,
      media: query,
      onchange: null,
      addListener: vi.fn(),
      removeListener: vi.fn(),
      addEventListener: vi.fn(),
      removeEventListener: vi.fn(),
      dispatchEvent: vi.fn(),
    })),
  });
}

const jasmineMock = {
  createSpyObj: (baseName: string, methodNames: any, propertyNames?: any) => {
    const obj: any = {};
    const methods = Array.isArray(methodNames) ? methodNames : (methodNames ? Object.keys(methodNames) : []);
    
    methods.forEach(method => {
      const spy: any = vi.fn();
      spy.and = {
        returnValue: (val: any) => {
          spy.mockReturnValue(val);
          return spy;
        },
        callFake: (fn: any) => {
          spy.mockImplementation(fn);
          return spy;
        },
        stub: () => spy
      };
      spy.calls = {
        mostRecent: () => ({
          args: spy.mock.calls[spy.mock.calls.length - 1] || []
        }),
        reset: () => {
          spy.mockClear();
        },
        count: () => spy.mock.calls.length,
        any: () => spy.mock.calls.length > 0,
        all: () => spy.mock.calls.map((c: any) => ({ args: c }))
      };
      obj[method] = spy;
      if (methodNames && !Array.isArray(methodNames)) {
        spy.mockReturnValue(methodNames[method]);
      }
    });

    const props = propertyNames;
    if (props) {
      Object.keys(props).forEach(prop => {
        Object.defineProperty(obj, prop, {
          get: () => props[prop],
          set: (val) => { props[prop] = val; },
          configurable: true,
          enumerable: true
        });
      });
    }

    return obj;
  }
};

(globalThis as any).jasmine = jasmineMock;

(globalThis as any).spyOn = (obj: any, methodName: string) => {
  const spy = vi.spyOn(obj, methodName);
  const spyAny = spy as any;
  spyAny.and = {
    returnValue: (val: any) => {
      spy.mockReturnValue(val);
      return spy;
    },
    callFake: (fn: any) => {
      spy.mockImplementation(fn);
      return spy;
    }
  };
  spyAny.calls = {
    mostRecent: () => ({
      args: spy.mock.calls[spy.mock.calls.length - 1] || []
    }),
    reset: () => {
      spy.mockClear();
    },
    count: () => spy.mock.calls.length,
    any: () => spy.mock.calls.length > 0,
    all: () => spy.mock.calls.map((c: any) => ({ args: c }))
  };
  return spy;
};
